package com.amorgens.trade.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.SellOrder
import com.amorgens.trade.domain.requests.CreateOrderMessageReq
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.theme.Blue_Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red


@Composable
fun singleOrderScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    id: String
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()
    val singleBuyOrder = orderViewModel.singleBuyOrder.collectAsState()
    val userName = orderViewModel.userName.collectAsState()
    val singleSellOrder = orderViewModel.singleSellOrder.collectAsState()
    val messages = orderViewModel.orderMessages.collectAsState()

    DisposableEffect(true) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getSingleBuyOrder(id)
                orderViewModel.getOrderMessages(id)
                orderViewModel.getUserName()
                Log.d("XXX SINGLE SELL ORDER ID", singleBuyOrder.value.sell_order_id)

            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            orderViewModel.clearModelData()
        }
    }

    val isExpanded = remember {
        mutableStateOf(false)
    }


    val message = remember {
        mutableStateOf("")
    }







    if(tradeStateUI.value.isConfirmBuyOrderError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.confirmBuyOrderErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetSingleOrderScreenState()

    }

    if(tradeStateUI.value.isCancelBuyOrderError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.cancelBuyOrderError, Toast.LENGTH_SHORT).show()

    }

    if(tradeStateUI.value.isCancelBuyOrderSuccess){
        Toast.makeText(LocalContext.current, "Order Cancelled!", Toast.LENGTH_SHORT).show()
        orderViewModel.getSingleBuyOrder(id)
    }

    if(tradeStateUI.value.isConfirmBuyOrderSuccess){
        Toast.makeText(LocalContext.current, "Order Confirmed", Toast.LENGTH_SHORT).show()
        orderViewModel.resetSingleOrderScreenState()
        orderViewModel.getSingleBuyOrder(id)
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Order details", navController = navController) }, floatingActionButton = { /*TODO*/ }) {
        Column (
            modifier = Modifier.fillMaxSize()
        ){

            // top buttons
            Row {
                // confirm button
                Button(onClick = {
                    //orderViewModel.login()
                    if (userName.value == singleBuyOrder.value.user_name){
                        orderViewModel.buyerConfirmBuyOrder(singleBuyOrder.value.id)
                    }else{
                        if (userName.value == singleSellOrder.value.user_name){
                            orderViewModel.sellerConfirmBuyOrder(singleBuyOrder.value.id)
                        }
                    }

                },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    enabled =    getIsConfirmButtonEnabled(userName, singleBuyOrder, singleSellOrder)
                ) {
                    if(tradeStateUI.value.isConfirmBuyOrderButtonLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Confirm Transaction",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }


                }

                // cancel button
                if (userName.value == singleBuyOrder.value.user_name){
                    Button(onClick = {
                        Log.d("CANCEL BUY ORDER ID", id.toString())
                        orderViewModel.cancelBuyOrder(id)
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        enabled = !singleBuyOrder.value.is_canceled
                    ) {
                        if(tradeStateUI.value.isCancelBuyOrderButtonLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                        }else {
                            Text(text = "Cancel",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }

            }

            // payment details
            Column {
                Row (
                    modifier = Modifier.clickable(onClick = {
                        if(isExpanded.value){
                            isExpanded.value = false
                        }else{
                            isExpanded.value = true
                        }
                    })
                ){
                    Text(text = "Payment Details ")
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")

                }
                if(isExpanded.value){
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Blue_Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = "Account Number: "+ (singleSellOrder.value.payment_method_data?.account_number))
                            Text(text = "Account Name: "+ singleSellOrder.value.payment_method_data?.account_name)
                            Text(text = "Bank Name: "+ singleSellOrder.value.payment_method_data?.bank_name)
                            Text(text = "Seller User Name: "+ singleSellOrder.value.user_name)
                        }
                    }

                }
            }

            // chat view
            val scrollState = rememberScrollState()
            Column (
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                messages.value.forEachIndexed { index, orderMessage ->
                    Card (
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(if(orderMessage.sender_user_name == userName.value) Alignment.End else Alignment.Start),
                        colors = CardDefaults.cardColors(containerColor =if(orderMessage.sender_user_name == userName.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = orderMessage.text,
                            color = if(orderMessage.sender_user_name == userName.value) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary ,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }

            // chat text box

            Row (

            ){
                OutlinedTextField(value = message.value,
                    onValueChange = {
                        message.value = it
                    },
                    shape = RoundedCornerShape(20.dp),
                    placeholder = { Text(text = "Message") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(end = 16.dp)
                )
                Button(onClick = {
                    orderViewModel.createOrderMessage(CreateOrderMessageReq(
                         receiver_user_name = if(singleBuyOrder.value.user_name == userName.value) singleSellOrder.value.user_name else singleBuyOrder.value.user_name,
                         singleBuyOrder.value.id,
                         message.value,
                         ""
                    ))
                    message.value = ""

                },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if(tradeStateUI.value.isCreateOrderMessageButtonLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Send",
                            color = MaterialTheme.colorScheme.surface)
                    }

                }

            }

        }
    }






}

fun getIsConfirmButtonEnabled(userName:State<String>, singleBuyOrder:State<BuyOrder>, singleSellOrder: State<SellOrder>):Boolean{
    if (userName.value == singleBuyOrder.value.user_name){
        Log.d("getIsConfirmButtonEnabled XX", "buyer logged in")
        //buyer logged in
        if(singleBuyOrder.value.is_buyer_confirmed){
            // buyer confirmed
            Log.d("getIsConfirmButtonEnabled XX", "buyer confirmed")
            return false
        }else {
            // buyer not confirmed
            Log.d("getIsConfirmButtonEnabled XX", "buyer not confirmed")
            return true
        }
    }else{
        Log.d("getIsConfirmButtonEnabled XX", "single sell order username ... "+singleSellOrder.value.user_name )
        if (userName.value == singleSellOrder.value.user_name){

            //seller logged in
            Log.d("getIsConfirmButtonEnabled XX", "seller logged in")
            if (singleBuyOrder.value.is_seller_confirmed){
                // seller confirmed
                Log.d("getIsConfirmButtonEnabled XX", "seller confirmed")
                return false
            }else {
                // seller not confirmed
                Log.d("getIsConfirmButtonEnabled XX", "seller not confirmed")
                return true
            }
        }else {
            Log.d("getIsConfirmButtonEnabled XX", "unknown user logged in  .. "+ userName.value)
            // unknown user logged in
            return  false
        }
    }
}