package com.amorgens.wallet.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.domain.GetBalanceReq
import com.amorgens.wallet.domain.GetWalletReq


@Composable
fun AddWalletScreen(navController: NavController, walletViewModel: WalletViewModel){
    // clear model data
    DisposableEffect(true) {
        //walletViewModel.clearModelData()
        onDispose {
            walletViewModel.clearModelData()
        }
    }
    val walletUIState = walletViewModel.walletUIState.collectAsState()

    //navController.popBackStack()
    // navigate back to previous screen after crating wallet and reset the navigate screen switch
    if(walletUIState.value.isAddWalletSuccess){

    }
    val context = LocalContext.current

    // show toast for success and error
    LaunchedEffect(walletUIState.value.isAddWalletSuccess) {
        if (walletUIState.value.isAddWalletSuccess){
            Toast.makeText(context, "Wallet Added Successfully", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    if (walletUIState.value.isAddWalletError){
        Toast.makeText(LocalContext.current,walletUIState.value.addWalletErrorMessage, Toast.LENGTH_SHORT).show()
    }
    val walletAddress = remember {
        mutableStateOf("")
    }
    val walletPassPhrase = remember{
        mutableStateOf("")
    }
//    LaunchedEffect(walletUIState.value) {
//        Log.d("XXX UPDATE", "Updated")
//    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Add Wallet", navController = navController) }, floatingActionButton = { /*TODO*/ }) {

        Column {
            Text(text = "Add an existing wallet")

            val amount = remember {
                mutableStateOf("")
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(value = walletAddress.value,
                        onValueChange = {
                                        walletAddress.value = it
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Wallet Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = amount.value,
                        onValueChange = {walletPassPhrase.value = it},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Pass Phrase") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )

                    Button(onClick = {
                        val getWalletreq = GetWalletReq(address = walletAddress.value)
                        walletViewModel.addWallet(getWalletreq)
                    },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),

                    ) {
                        if(walletUIState.value.isAddWalletButtonLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                        }else {
                            Text(text = "Add")
                            Icon(imageVector = Icons.Sharp.Add, contentDescription = "Add" )
                        }

                    }
                }
            }
        }
    }
}