package com.amorgens.trade.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.SellOrder
import com.amorgens.trade.domain.requests.CreateBuyOrderReq
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.theme.Black
import com.amorgens.ui.theme.Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red
import com.amorgens.wallet.presentation.WalletListItem
import java.math.BigDecimal


@Composable
fun sellOrderList(
    navController: NavController,
    openOrders:List<SellOrder>,
    orderViewModel: OrderViewModel,
    address:String
){
    val orders = listOf(1,9,90,0)
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxHeight()
    ) {
        items(openOrders){order->
           sellOrderListItem(navController, order, orderViewModel, address)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun sellOrderListItem(
    navController: NavController,
    order:SellOrder,
    orderViewModel: OrderViewModel,
    address:String
){
    val modalBottomSheetState = rememberModalBottomSheetState(

    )
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()
    val buyOrder = orderViewModel.buyOrder.collectAsState()

    if(tradeStateUI.value.isCreateBuyOrderSuccess){
        // navigate to single buy order page
        navController.navigate(NavScreen.SingleOrderScreen.route+"/"+buyOrder.value.id)
        Log.d("NEW BUY ORDER", NavScreen.SingleOrderScreen.route+"/"+buyOrder.value.id)
        orderViewModel.resetCreateBuyOrderScreen()
    }

    val amount = remember {
        mutableStateOf("")
    }
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                //navController.navigate(NavScreen.SingleOrderScreen.route+"/"+order.id)

            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (openBottomSheet.value){
                ModalBottomSheet(
                    onDismissRequest = {openBottomSheet.value = false},
                    sheetState = modalBottomSheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() },
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),

                    ) {
                        Text(text = order.user_name, style = MaterialTheme.typography.titleLarge)
                        Text(text = "kuracoin available: "+ String.format("%,.2f",order.amount))
                        Text(text = "NGN "+String.format("%,.2f", orderViewModel.getExchangeValue(order.amount)),)
                        OutlinedTextField(value = amount.value,
                            onValueChange = {
                                amount.value = it
                            },
                            shape = RoundedCornerShape(20.dp),
                            placeholder = { Text(text = "Amount") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        Button(onClick = {

                            if (!formValidation(context, amount = amount.value)){
                                return@Button
                            }
                            // lgoin with token, this is temporary
                            //orderViewModel.login()

                            orderViewModel.createBuyOrder(
                                CreateBuyOrderReq(BigDecimal(amount.value), order.id, address)
                            )
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.size(width = 200.dp, height = 50.dp)
                        ) {
                            if(tradeStateUI.value.isCreateBuyOrderButtonLoading){
                                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                            }else {
                                Icon(imageVector = Icons.Sharp.AddCard, contentDescription = "Create" )
                                Text(text = "Buy")
                            }
                        }

                    }

                }
            }


            // is online or offline
            Row(
            ) {
                Icon(imageVector = Icons.Filled.Circle,
                    contentDescription = "",
                    tint = Green,
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
                    Text(text = String.format("%,.2f", order.amount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text =  "NGN "+String.format("%,.2f", orderViewModel.getExchangeValue(order.amount)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }
            // ratings
            Row (
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

                Button(
                    onClick = {openBottomSheet.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.size(width = 100.dp, height = 40.dp),


                    ) {

                    Text(text = "Buy", style = MaterialTheme.typography.titleMedium)
                }
            }

        }
    }

}

fun formValidation(context:Context, amount:String):Boolean{

    if(amount.isBlank() || amount.isEmpty()){
        Toast.makeText(context, "invalid amount", Toast.LENGTH_SHORT).show()
        return false
    }

    try {
        BigDecimal(amount)
    }catch (e:Exception){
        Toast.makeText(context, "invalid numeric data", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}