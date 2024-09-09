package com.amorgens.trade.presentation

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.amorgens.NavScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.PaymentMethodData
import com.amorgens.trade.domain.requests.CreatePaymentMethod
import com.amorgens.trade.domain.requests.PaymentMethod
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.wallet.domain.GetWalletReq


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun paymentOptionScreen(
    orderViewModel: OrderViewModel,
    navHostController: NavController
){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                orderViewModel.getAllMyPaymentMethods()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            orderViewModel.clearModelData()
        }
    }

    val open = remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()

    val paymentMethods = orderViewModel.paymentMethodDatas.collectAsState()
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()

    if(tradeStateUI.value.isDeletePaymentMethodError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.deletePaymentMethodErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetGetPaymentMethodScreen()
    }
    if(tradeStateUI.value.isGetPaymentMethodsError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.getPyamentMethodsErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetGetPaymentMethodScreen()
    }
    if(tradeStateUI.value.isDeletePaymentMethodSuccess){
        Toast.makeText(LocalContext.current, "Deleted!", Toast.LENGTH_SHORT).show()
        orderViewModel.getAllMyPaymentMethods()
        orderViewModel.resetGetPaymentMethodScreen()

    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Payment Methods", navHostController )}, floatingActionButton = { /*TODO*/ }) {
        Column(

            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)

        ){
            //
            Text(text = "Payment options")
            paymentMethods.value.forEachIndexed { index, paymentMethodData ->
                PaymentMethodItem(method = paymentMethodData, orderViewModel)
            }

            Button(onClick = {
                navHostController.navigate(NavScreen.CreatePaymentMethodScreen.route)
            },
                modifier = Modifier.size(width = 200.dp, height = 50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                Text(text = "Add Payment Method")
            }
        }
    }

}


@Composable
fun PaymentMethodItem(method:PaymentMethodData, orderViewModel: OrderViewModel){
    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "GTB 1", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                orderViewModel.deletePaymentMethods(method.id)
            }) {
                if(orderViewModel.tradeUIState.collectAsState().value.isDeletePaymentMethodButtonLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Icon(imageVector = Icons.Default.Delete, contentDescription ="Delete" )
                }

            }
        }
        Text(text = "Bank Name: " + method.bank_name)
        Text(text = "Account Name: "+ method.account_name)
        Text(text = "Account Number: "+ method.account_number)
        Text(text = "Other :"+method.other)
    }
}