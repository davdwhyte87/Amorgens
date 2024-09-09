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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Rocket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.domain.GetWalletReq
import com.amorgens.wallet.domain.TransferReq
import java.math.BigDecimal
import java.util.UUID


@Composable
fun TransferScreen(
    navController: NavController,
    walletAddress:String,
    walletViewModel: WalletViewModel
){
    // clear model data
    DisposableEffect(true) {
        //walletViewModel.clearModelData()
        onDispose {
            walletViewModel.clearModelData()
        }
    }

    val walletUIState = walletViewModel._walletUIState.collectAsState().value

    val walletPassPhrase = remember{
        mutableStateOf("")
    }
    val amount = remember{
        mutableStateOf("")
    }
    val receiver_address = remember{
        mutableStateOf("")
    }

    // show toast for success and error

    if (walletUIState.isTransferSuccessful){
        Toast.makeText(LocalContext.current, "OK!", Toast.LENGTH_SHORT).show()
        walletViewModel.clearModelData()
    }
    if (walletUIState.isTransferError){
        Toast.makeText(LocalContext.current,walletUIState.transferErrorMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearModelData()
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Transfer", navController = navController) }, floatingActionButton = { /*TODO*/ }) {

        Column {
            Text(text = walletAddress,
                style = MaterialTheme.typography.titleLarge
            )

            Box(modifier = Modifier.fillMaxWidth()){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(value = amount.value,
                        onValueChange = {
                            amount.value =it
                                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Amount")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = receiver_address.value,
                        onValueChange = {
                                        receiver_address.value = it
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Wallet Address")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = walletPassPhrase.value,
                        onValueChange = {walletPassPhrase.value = it},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Pass Phrase")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )

                    Button(onClick = {
                        walletViewModel.transfer(TransferReq(
                                         sender = walletAddress.lowercase(),
                                         receiver = receiver_address.value.lowercase(),
                                         amount = amount.value,
                                         sender_password = walletPassPhrase.value,
                                         transaction_id = UUID.randomUUID().toString()
                                     ))

                    },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        enabled = if (walletUIState.isTransferButtonLoading){false}else{true}

                        ) {
                        if(walletUIState.isTransferButtonLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                        }else {
                            Text(text = "Transfer")
                            Icon(imageVector = Icons.Sharp.Rocket, contentDescription = "Send" )
                        }
                    }
                }
            }
        }
    }
}