package com.amorgens.auth.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.amorgens.NavScreen
import com.amorgens.auth.data.AuthViewModel
import com.amorgens.auth.domain.SignupReq
import com.amorgens.ui.AnimatedPreloader


@Composable
fun signUpScreen(
    authViewModel: AuthViewModel,
    navHostController: NavController
){
    val userName = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }
    val authUIState = authViewModel.authUIState.collectAsState()
    val context = LocalContext.current

    if(authUIState.value.isSignupError){
        Toast.makeText(LocalContext.current, authUIState.value.signupErrorMessage, Toast.LENGTH_SHORT).show()
        authViewModel.resetSignupUIState()
    }
    if(authUIState.value.isSignupSuccess){
        Toast.makeText(LocalContext.current, "Success. You can login now!", Toast.LENGTH_SHORT).show()
        userName.value = ""
        password.value = ""
        authViewModel.resetSignupUIState()
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedTextField(value = userName.value,
            onValueChange = {
                userName.value = it
            },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        OutlinedTextField(value = password.value,
            onValueChange = {
                password.value = it
            },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )

        Button(onClick = {
            if (!validateInput(userName.value, password.value, context)){
                return@Button
            }
            val signupReq = SignupReq(
                user_name =userName.value,
                password=password.value
            )
            authViewModel.signup(signupReq)

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(width = 200.dp, height = 50.dp)
        ) {

            if(authUIState.value.isSignupButtonLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
            }else {
                Text(text = "Signup",
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }
        Text(text = "Login",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = {
                navHostController.navigate(NavScreen.LoginScreen.route)
            })
        )
    }
}

fun validateInput(userName:String, password:String, context: Context):Boolean{
    val isAllLowerCase = userName.all { it.isLowerCase() }
//    if (!isAllLowerCase){
//        Toast.makeText(context, "username should be all lowercase", Toast.LENGTH_SHORT).show()
//        return false
//    }
    val hasSpace = userName.any { it.isWhitespace() }
    if (hasSpace){
        Toast.makeText(context, "username should have no spaces", Toast.LENGTH_SHORT).show()
        return false
    }
    if (userName.isBlank()){
        Toast.makeText(context, "username cannot be blank", Toast.LENGTH_SHORT).show()
        return false
    }
    if (password.isBlank()){
        Toast.makeText(context, "password cannot be blank", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}