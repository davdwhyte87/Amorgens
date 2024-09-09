package com.amorgens.trade.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.SellOrder
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.ui.theme.Black
import com.amorgens.ui.theme.Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red


@Composable
fun myOrdersScreen(
    navController: NavController,
    ordersViewModel: OrderViewModel
){
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(true) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                ordersViewModel.getMySellOrders()
                ordersViewModel.getMyBuyOrders()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            ordersViewModel.clearModelData()
        }
    }

    val sellOrders =ordersViewModel.mySellOrder.collectAsState()
    val buyOrders = ordersViewModel.myBuyOrders.collectAsState()


    val context = LocalContext.current
    val tradeStateUI = ordersViewModel.tradeUIState.collectAsState()

//    if(tradeStateUI.value.isError){
//        Toast.makeText(LocalContext.current, tradeStateUI.value.errorMessage, Toast.LENGTH_SHORT).show()
//        ordersViewModel.resetSuccessAndError()
//    }


    GeneralScaffold(topBar = { BackTopBar(pageName = "My Orders", navController ) }, floatingActionButton = { /*TODO*/ }) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Text(text = "Sell Orders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )


            mySellOrdersList(navController, sellOrders.value, ordersViewModel)
            Text(text = "Buy Orders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            myBuyOrdersList(navController, buyOrders.value, ordersViewModel)
        }
    }
}



@Composable
fun mySellOrdersList(
    navController: NavController,
    sellOrders:List<SellOrder>,
    ordersViewModel: OrderViewModel
){
    val items = listOf(1, 2)
    if(sellOrders.isNotEmpty()){
        sellOrders.forEachIndexed { index,   i ->
            mySellOrderListItem(navController, i, ordersViewModel)
        }
    }else{
        Text(text = "No sell orders")
    }
}

@Composable
fun mySellOrderListItem(
    navController: NavController,
    order:SellOrder,
    ordersViewModel: OrderViewModel
){
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleSellOrderScreen.route + "/" + order.id)
            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // order information
            Row  (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                // username and limit
                Column {
                    Text(text = order.user_name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "Limit : Min"+String.format("%,.2f", order.min_amount )+" - Max "+String.format("%,.2f", order.max_amount)+ " ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                // amount and value
                Column (

                ) {
                    Text(text =String.format("%,.2f", order.amount)+ " Kc",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "NGN "+String.format("%,.2f", ordersViewModel.getExchangeValue(order.amount)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }

        }
    }

}


@Composable
fun myBuyOrdersList(
    navController: NavController,
    buyOrders:List<BuyOrder>,
    ordersViewModel: OrderViewModel
){
    val items = listOf(1, 2)
    if (buyOrders.isNotEmpty()) {
        buyOrders.forEachIndexed { index, buyOrder ->

            myBuyOrderListItem(navController, buyOrder, ordersViewModel)
        }
    }else{
        Text(text = "No buy order")
    }
}

@Composable
fun myBuyOrderListItem(
    navController: NavController,
    buyOrder:BuyOrder,
    ordersViewModel: OrderViewModel
){
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleOrderScreen.route + "/" + buyOrder.id)
            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            // order information
            Row  (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                // username and limit
                Column {
                    Text(text = buyOrder.user_name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
//                    Text(text = "Limit : Min 200,000 - Max 5,000,000",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
                }
                // amount and value
                Column (

                ) {
                    Text(text =String.format("%,.2f", buyOrder.amount )+ " Kc",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "NGN "+String.format("%,.2f", ordersViewModel.getExchangeValue(buyOrder.amount)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }

        }
    }

}