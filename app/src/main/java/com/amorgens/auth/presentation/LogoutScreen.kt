package com.amorgens.auth.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.amorgens.NavScreen
import com.amorgens.auth.data.AuthViewModel
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import com.amorgens.trade.presentation.formValidation
import com.amorgens.ui.AnimatedPreloader
import kotlinx.coroutines.launch
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun logoutScreen(
    navHostController: NavController,
    authViewModel: AuthViewModel
){
    val sheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {  coroutine.launch { sheetState.show() }},
        sheetState = sheetState,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(50.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // text
            Text(text = "Are You Sure You Want To Logout? ",
                style = MaterialTheme.typography.titleLarge
            )
            // signup button
            Button(onClick = {
                authViewModel.logout()
                navHostController.navigate(NavScreen.LoginScreen.route)
                             },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                Text(text = "Logout",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // back button
            Button(onClick = {
                navHostController.popBackStack()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                Text(text = "Cancel",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}