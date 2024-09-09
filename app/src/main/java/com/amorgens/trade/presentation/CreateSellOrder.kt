package com.amorgens.trade.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.PaymentMethodData
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.ui.currencyDropDown
import java.math.BigDecimal


@Composable
fun createSellOrderScreen(
    navController:NavController,
    orderViewModel: OrderViewModel,
    address:String
){
    // clear model data
    DisposableEffect(true) {
        //orderViewModel.clearModelData()
        onDispose {
            orderViewModel.clearModelData()
        }
    }
    val amount = remember {
        mutableStateOf("")
    }
    val minAmount = remember {
        mutableStateOf("")
    }
    val maxAmount = remember {
        mutableStateOf("")
    }

    val contactPhone = remember {
        mutableStateOf("")
    }
    val walletPassword = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()


    if(tradeStateUI.value.isCreateSellOrderError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.createSellOrderErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetCreateSellOrderScreen()
    }
    if(tradeStateUI.value.isCreateSellOrderSuccess){
        Toast.makeText(LocalContext.current, "Created!", Toast.LENGTH_SHORT).show()

        orderViewModel.resetCreateSellOrderScreen()
        navController.popBackStack()
    }

    LaunchedEffect(true) {
        orderViewModel.getAllMyPaymentMethods()
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(PaymentMethodData(name = "Payment Method")) }
    val options = orderViewModel.paymentMethodDatas.collectAsState()

    GeneralScaffold(topBar = { BackTopBar(pageName = "Sell Order", navController = navController) }, floatingActionButton = {  }) {
        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                    .padding(end = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
//            Text(text = "21,000 NGN")
//
//            currencyDropDown(title = "Currency")

            OutlinedTextField(value = minAmount.value,
                onValueChange = {
                    minAmount.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Min Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )

            OutlinedTextField(value = contactPhone.value,
                onValueChange = {
                    contactPhone.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Contact Phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )


            OutlinedTextField(value = walletPassword.value,
                onValueChange = {
                    walletPassword.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Wallet Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )

            // payment option
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)) {
                Row {
                    Text(
                        text = selectedOption.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(4.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary)
                    )
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Payment Option")
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.value.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            // button
            Button(onClick = {

                if (!formValidation(context,
                        minAmount = minAmount.value,
                        phone = contactPhone.value,
                        amount = amount.value,
                        selectedOption = selectedOption.id
                )){
                    return@Button
                }
                // lgoin with token, this is temporary
                //orderViewModel.login()

               orderViewModel.createSellOrder(CreateSellOrderReq(
                   BigDecimal(amount.value),
                   BigDecimal(minAmount.value),
                   Currency.NGN,
                   PaymentMethod.Bank,
                   selectedOption.id,
                   address,
                   contactPhone.value,
                   walletPassword.value
               ))
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                if(tradeStateUI.value.isCreateSellOrderButtonLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Icon(imageVector = Icons.Sharp.AddCard, contentDescription = "Create" )
                    Text(text = "Create")
                }

            }
        }
    }
}

data class FormError(
    val isSuccess: Boolean,
    val message: String = ""
)

fun formValidation(context:Context ,minAmount:String, phone:String, amount:String, selectedOption:String):Boolean{
    if(minAmount.isBlank() || minAmount.isEmpty()){
        Toast.makeText(context, "invalid min amount", Toast.LENGTH_SHORT).show()
        return false
    }
//    if(maxAmount.isBlank() || maxAmount.isEmpty()){
//        Toast.makeText(context, "invalid max amount", Toast.LENGTH_SHORT).show()
//        return false
//    }

    if(phone.isBlank() || phone.isEmpty()){
        Toast.makeText(context, "invalid phone number", Toast.LENGTH_SHORT).show()
        return false
    }
    if(amount.isBlank() || amount.isEmpty()){
        Toast.makeText(context, "invalid min amount", Toast.LENGTH_SHORT).show()
        return false
    }

    if(selectedOption.isBlank() || selectedOption.isEmpty()){
        Toast.makeText(context, "invalid payment option", Toast.LENGTH_SHORT).show()
        return false
    }


    try {
        BigDecimal(amount)
        BigDecimal(minAmount)
//        BigDecimal(maxAmount)


    }catch (e:Exception){
        Toast.makeText(context, "invalid numeric data", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}

