package com.amorgens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.NavScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(pageName:String, navController: NavController){
    CenterAlignedTopAppBar(
        title = { Text(text = pageName, textAlign = TextAlign.Center)},
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(NavScreen.HomeScreen.route)
            }) {
                Icon(imageVector = Icons.Outlined.Home ,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarWithOptions(pageName:String, navController: NavController){
    val sheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()
    val open = remember {
        mutableStateOf(false)
    }

    CenterAlignedTopAppBar(
        title = { Text(text = pageName, textAlign = TextAlign.Center)},
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(NavScreen.HomeScreen.route)
            }) {
                Icon(imageVector = Icons.Outlined.Home ,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if(open.value){
                    open.value = false
                }else{
                    open.value = true
                }

            }) {
                Icon(imageVector = Icons.Outlined.MoreVert ,
                    contentDescription = "Option",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )


    if (open.value){
        ModalBottomSheet(
            onDismissRequest = {  open.value = false},
            sheetState = sheetState,
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(50.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                // text
                Text(text = "Wallet Configuration Options",
                    style = MaterialTheme.typography.titleMedium
                )
                // signup button
                Button(onClick = {navController.navigate(NavScreen.MyPaymentMethodsScreen.route)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.size(width = 200.dp, height = 50.dp)
                ) {
                    Text(text = "Payment Option",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // login button
//                Button(onClick = {navHostController.navigate(NavScreen.LoginScreen.route)},
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = MaterialTheme.colorScheme.surface
//                    ),
//                    modifier = Modifier.size(width = 200.dp, height = 50.dp)
//                ) {
//                    Text(text = "Login",
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                }
            }
        }
    }

}