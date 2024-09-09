package com.amorgens.wallet.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import com.airbnb.lottie.parser.IntegerParser
import com.amorgens.NavScreen
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.ui.HomeTopBarWithOptions
import com.amorgens.wallet.data.WalletViewModel
import java.io.File.separator
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale


@Composable
fun WalletScreen(navController: NavController, walletViewModel: WalletViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                walletViewModel.getAllWallets()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            //walletViewModel.clearModelData()
        }
    }


    // get all wallets
//    LaunchedEffect(true) {
//        walletViewModel.getAllWallets()
//    }


    val wallets = walletViewModel.allWallets.collectAsState().value

    // update wallet locally
    LaunchedEffect(Unit) {
        //walletViewModel.getAllWallets()
        val walletNames = mutableListOf<String>()

//        wallets.forEachIndexed { index, wallet ->
//            walletNames.add(wallet.walletAddress)
//        }
//        Log.d("WALLET NAMES XXXX", walletNames.toString())
//        walletViewModel.updateWalletsLocal(walletNames)
    }

    val walletUIState = walletViewModel.walletUIState.collectAsState().value


    // show error toast if loading wallets data fails
    if (walletUIState.isError){
        Log.d("XXXXX GOT ERROR", "YEt")
        Toast.makeText(LocalContext.current,walletUIState.errorMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearError()
    }

    var totalBalance = BigDecimal("0.0")

    wallets.forEachIndexed { index, wallet ->
        totalBalance = totalBalance.add(wallet.balance)
        //Log.d("XXDECIMAL", "${scaledBigDecimal}")
    }
    Log.d("XXFLOAT ${wallets.getOrNull(0)?.balance}", "totalBalance")
    GeneralScaffold(topBar = { HomeTopBarWithOptions("Wallets", navController) }, floatingActionButton = {  }) {
        var isExpanded = remember {
            mutableStateOf(false)
        }
        Column  (
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){


            ElevatedCard(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column (
                        modifier = Modifier
                            .padding(1.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Text(text =String.format("%,.2f", totalBalance) ,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(text = "Total assets",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Row (
                        modifier = Modifier
                            .padding(1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text =  "NGN "+String.format("%,.2f", getExchangeValue(LocalContext.current, totalBalance)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Box (
                            modifier = Modifier.clickable(onClick = {
                                if (isExpanded.value) isExpanded.value = false else isExpanded.value = true
                            })
                        ){
                            Row {
                               Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription ="" )
                                Text(text = "NGN",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        DropdownMenu(expanded = isExpanded.value, onDismissRequest = { isExpanded.value = false }) {
                            DropdownMenuItem(text = { Text(text = "USD")}, onClick = {  })
                        }
                    }

                }
            }

            Row {

                val menus = listOf(
                    WalletMenu("Add", Icons.Default.Add),
                    WalletMenu("New", Icons.Default.AddCard),
                    WalletMenu("Orders", Icons.Default.Storage)
                )
                menus.forEachIndexed { index, walletMenu ->
                    WalletMenuItem(iconImage = walletMenu.icon , menuTitle = walletMenu.name) {
                        if (index == 0){
                            navController.navigate(NavScreen.AddWalletScreen.route)
                        }

                        if (index ==1){
                            navController.navigate(NavScreen.NewWalletScreen.route)
                        }

                        if (index==2){
                            navController.navigate(NavScreen.MyOrdersScreen.route)
                        }

                    }
                }
            }

            Log.d("Syncing?? XXX", walletUIState.isSyncingLocalWallet.toString())
            if (walletUIState.isSyncingLocalWallet){

                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
            }

            WalletList(navController,wallets)
        }
    }
}

