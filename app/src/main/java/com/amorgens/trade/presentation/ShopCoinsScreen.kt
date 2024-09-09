package com.amorgens.trade.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.wallet.domain.CreateWalletReq
import com.amorgens.wallet.presentation.getExchangeValue
import java.math.BigDecimal


@Composable
fun ShopCoinsScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    address:String
){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {
       // orderViewModel.clearModelData()

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getOpenOrders()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            orderViewModel.clearModelData()
        }
    }

    val amount = remember {
        mutableStateOf("")
    }



    val openOrders = orderViewModel.openSellOrders.collectAsState()

    GeneralScaffold(topBar = { HomeTopBar(pageName = "Shop Coins", navController = navController) }, floatingActionButton = { /*TODO*/ }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            // text field
            OutlinedTextField(value = amount.value,
                onValueChange = {
                    amount.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            Text(text = "1 Vhenncoin =="+getExchangeValue(LocalContext.current, BigDecimal("1"))+ "NGN")


            // button
            Button(onClick = {
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                Text(text = "Buy")
            }

            // sell order list

            sellOrderList(navController, openOrders.value, orderViewModel, address)

        }
    }

}