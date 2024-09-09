package com.amorgens.trade.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.ui.theme.Black
import com.amorgens.ui.theme.Blue_Gray
import com.amorgens.ui.theme.Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red


@Composable
fun singleSellOrderScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    id:String
){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getSingleSellOrders(id)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            orderViewModel.clearModelData()
        }
    }
    // clear UI data
    orderViewModel.resetSuccessAndError()
    val isCancelButtonEnabled = remember { mutableStateOf(true) }
//    LaunchedEffect(true) {
//        orderViewModel.getSingleSellOrders(id)
//    }
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()
    if(tradeStateUI.value.isGetSingleSellOrderPageError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.getSingleSellOrderPageErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetSuccessAndError()
    }

    if(tradeStateUI.value.isCancelSellOrderError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.cancelSellOrderErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetSingleSellOrderScreenUI()
    }

    if (tradeStateUI.value.isCancelSellOrderSuccess){
        Toast.makeText(LocalContext.current, "Order Cancelled", Toast.LENGTH_SHORT).show()
        orderViewModel.getSingleSellOrders(id)
        orderViewModel.resetSingleSellOrderScreenUI()
    }

//    if(tradeStateUI.value.isSuccess){
//        Toast.makeText(LocalContext.current, "Ok", Toast.LENGTH_SHORT).show()
//        orderViewModel.resetSuccessAndError()
//    }

    val singleSellOrder = orderViewModel.singleSellOrder.collectAsState()
    if (singleSellOrder.value.is_closed){
        isCancelButtonEnabled.value = false
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Sell Order", navController ) }, floatingActionButton = { /*TODO*/ }) {

        val isExpanded = remember {
            mutableStateOf(false)
        }

        Column {

            Row(
                modifier = Modifier
                    .padding(1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%,.2f",singleSellOrder.value.amount),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Box(
                    modifier = Modifier
                        .clickable(onClick = {
                            if (isExpanded.value) isExpanded.value =
                                false else isExpanded.value = true
                        })
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = ""
                        )
                        Text(
                            text = "NGN",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                DropdownMenu(
                    expanded = isExpanded.value,
                    onDismissRequest = { isExpanded.value = false }) {
                    DropdownMenuItem(text = { Text(text = "NGN") }, onClick = { })
                }

                Text(
                    text = String.format("%,.2f",orderViewModel.getExchangeValue(singleSellOrder.value.amount)),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Button(
                    onClick = {
                        orderViewModel.cancelSellOrder(id)
                        //isCancelButtonEnabled.value= false
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    enabled = !singleSellOrder.value.is_closed
                ) {


                    if(tradeStateUI.value.isCancelSellOrderButtonLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Cancel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }

                }
            }

            Text(text = "Payment Information",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Blue_Gray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.padding(10.dp)
                ) {


                    Text(text = "Account Number: "+ singleSellOrder.value.payment_method_data?.account_number)
                    Text(text = "Account Name: "+ singleSellOrder.value.payment_method_data?.account_name)
                    Text(text = "Bank Name: " + singleSellOrder.value.payment_method_data?.bank_name)

                }
            }

            Text(text = "All Buy Orders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            allBuyOrderListItem(navController, singleSellOrder.value.buy_orders, orderViewModel)
        }
    }
}

@Composable
fun allBuyOrderListItem(
    navController: NavController,
    buyOrders : List<BuyOrder>,
    orderViewModel: OrderViewModel
){
    buyOrders.forEachIndexed { index, buyOrder ->
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

                // is online or offline
                Row(
                ) {
                    Icon(imageVector = Icons.Filled.Circle,
                        contentDescription = "",
                        tint = getColorTradeStatus(buyOrder),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(2.dp)
                    )
                    Text(text = "Online", color = MaterialTheme.colorScheme.secondary)
                }
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
                        Text(text = "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    // amount and value
                    Column (

                    ) {
                        Text(text = String.format("%,.2f", buyOrder.amount) + " Kc",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(text = "NGN "+String.format("%,.2f", orderViewModel.getExchangeValue(buyOrder.amount)),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }
                // ratings
                Row (
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    // rating up
                    Row (
                        modifier = Modifier.padding(5.dp)
                    ){
                        Icon(imageVector = Icons.Filled.ThumbUp,
                            contentDescription = "rating",
                            tint = Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "100",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    //reting down
                    Row (
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.ThumbDown,
                            contentDescription = "rating",
                            tint = Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "2",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }


}

fun getColorTradeStatus(buyOrder: BuyOrder):Color{
    if (buyOrder.is_seller_confirmed && buyOrder.is_buyer_confirmed){
        return  Color.Blue
    }
    if (buyOrder.is_canceled || buyOrder.is_reported){
        return Red
    }
    if (buyOrder.is_buyer_confirmed || buyOrder.is_seller_confirmed){
        return  Color.Green
    }

    return Gray
}