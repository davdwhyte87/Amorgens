package com.amorgens.trade.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.requests.CreatePaymentMethod
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import java.math.BigDecimal

@Composable
fun addPaymentMethodScreen(
    navHostController: NavController,
    orderViewModel:OrderViewModel
){
    // clear model data
    DisposableEffect(true) {
        //orderViewModel.clearModelData()
       onDispose {
           orderViewModel.clearModelData()
       }
    }
    val bankName = remember {
        mutableStateOf("")
    }
    val accountName = remember {
        mutableStateOf("")
    }
    val accountNumber = remember {
        mutableStateOf("")
    }
    val other = remember {
        mutableStateOf("")
    }

    val name = remember {
        mutableStateOf("")
    }
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()
    val context = LocalContext.current

    if(tradeStateUI.value.isAddPaymentMethodsError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.addPyamentMethodsErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetAddPaymentMethodScreen()
    }
    if(tradeStateUI.value.isAddPaymentMethodsSuccess){
        Toast.makeText(LocalContext.current, "Created!", Toast.LENGTH_SHORT).show()

        orderViewModel.resetAddPaymentMethodScreen()
        navHostController.popBackStack()
    }

    GeneralScaffold(topBar = {BackTopBar(pageName = "Add Payment Method", navHostController)}, floatingActionButton = { /*TODO*/ }) {


        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Add Bank Payment Method",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(value = name.value,
                onValueChange = {
                    name.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Payment Method Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            OutlinedTextField(value = bankName.value,
                onValueChange = {
                    bankName.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Bank Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            OutlinedTextField(value = accountName.value,
                onValueChange = {
                    accountName.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Account Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            OutlinedTextField(value = accountNumber.value,
                onValueChange = {
                    accountNumber.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Account number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            OutlinedTextField(value = other.value,
                onValueChange = {
                    other.value = it
                },
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text(text = "Other") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            )
            // signup button
            Button(
                onClick = {
                    if (!addPaymentFormValidation(context,
                        name.value,
                        bankName.value,
                        accountName.value,
                        accountNumber.value
                    )){
                        return@Button
                    }
                    val createPaymentMethodReq = CreatePaymentMethod(
                        account_name = accountName.value,
                        account_number = accountNumber.value,
                        bank_name = bankName.value,
                        other = other.value,
                        name = name.value
                    )

                    orderViewModel.addPaymentMethods(createPaymentMethodReq)

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                if(tradeStateUI.value.isAddPaymentMethodLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Text(
                        text = "Add Payment Option",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }
        }
    }

}

fun addPaymentFormValidation(
    context: Context,
    name:String,
    bankName:String,
    accountNumber:String,
    accountName:String
    ):Boolean{
    if(name.isBlank() || name.isEmpty()){
        Toast.makeText(context, "invalid payment name", Toast.LENGTH_SHORT).show()
        return false
    }

    if(bankName.isBlank() || bankName.isEmpty()){
        Toast.makeText(context, "invalid bank name", Toast.LENGTH_SHORT).show()
        return false
    }
    if(accountName.isBlank() || accountName.isEmpty()){
        Toast.makeText(context, "invalid  account name", Toast.LENGTH_SHORT).show()
        return false
    }


    if(accountNumber.isBlank() || accountNumber.isEmpty()){
        Toast.makeText(context, "invalid  account number", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}