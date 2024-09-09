package com.amorgens.auth.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amorgens.auth.domain.AuthUIState
import com.amorgens.auth.domain.LoginReq
import com.amorgens.auth.domain.SignupReq
import com.amorgens.general.data.APIService
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.response.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthViewModel  @Inject constructor(
    private val apiService: APIService,
    private val application: Application
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken= _userToken.asStateFlow()

    private val _authUIState = MutableStateFlow(AuthUIState())
    val authUIState = _authUIState.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()


    fun signup(data: SignupReq){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // start loading button
                _authUIState.update { it.copy(isSignupButtonLoading = true) }

                // send data to api

                try {
                    val resp = apiService.signup(data)
                    if (resp.isSuccessful){
                        _authUIState.update { it.copy(
                            isSignupButtonLoading = false,
                            isSignupSuccess = true,
                            isSignupError = false,
                            signupErrorMessage = ""
                        ) }

                        Log.d("SIGNUP RESP", resp.body().toString())

                    }else{
                        Log.d("SIGNUP ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            isSignupButtonLoading = false,
                            isSignupSuccess = false,
                            isSignupError = true,
                            signupErrorMessage = errorResp.message
                        ) }

                        Log.d("SIGNUP ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        isSignupButtonLoading = false,
                        isSignupSuccess = false,
                        isSignupError = true,
                        signupErrorMessage = "Network Error"
                    ) }
                    Log.d("SIGNUP ERROR", e.toString())
                }

                _authUIState.update { it.copy(isSignupButtonLoading = true) }
            }
        }
    }


    fun login(data: LoginReq){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // start loading button
                _authUIState.update { it.copy(isLoginButtonLoading = true) }

                // send data to api

                try {
                    val resp = apiService.login(data)
                    if (resp.isSuccessful){
                        _authUIState.update { it.copy(
                            isLoginButtonLoading = false,
                            isLoginSuccess = true,
                            isLoginError = false,
                            loginErrorMessage = ""
                        ) }
                        // save token to local
                        loginMani(resp.body()?.data ?: "")
                        // save username to device
                        saveUserName(data.user_name)
                        Log.d("LOGIN RESP", resp.body().toString())

                    }else{
                        Log.d("LOGIN ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            isLoginButtonLoading = false,
                            isLoginSuccess = false,
                            isLoginError = true,
                            loginErrorMessage = errorResp.message
                        ) }

                        Log.d("LOGIN ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        isLoginButtonLoading = false,
                        isLoginSuccess = false,
                        isLoginError = true,
                        loginErrorMessage = "Network Error"
                    ) }
                    Log.d("LOGIN ERROR", e.toString())
                }

                _authUIState.update { it.copy(isLoginButtonLoading = false) }
            }
        }
    }

    fun isLoggedIn():Boolean{
        if(getUserToken().isBlank()){
            return false
        }

        return true
    }

    fun getUserName(application: Context){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }

    fun getUserToken():String{
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val token = sharedPreferences.getString("auth_token", null)

        Log.d("GET TOKEN", token ?:"")
        return token ?: ""

    }

    fun loginMani(token:String){
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }
    fun logout(){
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()

        // clear username
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val uNameeEdit = mshared.edit()
        uNameeEdit.remove("user_name")
        uNameeEdit.apply()
    }


    fun saveUserName(userName:String){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val edit = mshared.edit()
        edit.putString("user_name", userName)
        edit.apply()
    }

    fun resetSignupUIState(){
        _authUIState.update { it.copy(
            isSignupButtonLoading = false,
            isSignupSuccess = false,
            isSignupError = false,
            signupErrorMessage = ""
        ) }
    }

    fun resetLoginUIState(){
        _authUIState.update { it.copy(
            isLoginButtonLoading = false,
            isLoginError = false,
            isLoginSuccess = false,
            loginErrorMessage = ""
        ) }
    }
}