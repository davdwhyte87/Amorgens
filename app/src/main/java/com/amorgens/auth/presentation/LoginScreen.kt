package com.amorgens.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.amorgens.auth.domain.LoginReq
import com.amorgens.auth.domain.SignupReq
import com.amorgens.ui.AnimatedPreloader

@Composable
fun loginScreen(
    navHostController: NavController,
    authViewModel:AuthViewModel
){

    val authUIState = authViewModel.authUIState.collectAsState()
    val context = LocalContext.current


    if(authUIState.value.isLoginError){
        Toast.makeText(LocalContext.current, authUIState.value.loginErrorMessage, Toast.LENGTH_SHORT).show()
        authViewModel.resetLoginUIState()
    }
    if(authUIState.value.isLoginSuccess){
        Toast.makeText(LocalContext.current, "Success!", Toast.LENGTH_SHORT).show()
        authViewModel.resetLoginUIState()

        navHostController.navigate(NavScreen.HomeScreen.route)
    }

    val userName = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
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
            val loginReq = LoginReq(
                user_name =userName.value,
                password=password.value
            )
            authViewModel.login(loginReq)
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(width = 200.dp, height = 50.dp)
        ) {
            if(authUIState.value.isLoginButtonLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
            }else {
                Text(text = "Login",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Text(text = "Signup",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = {
               navHostController.navigate(NavScreen.SignupScreen.route)
            })
        )
    }
}


