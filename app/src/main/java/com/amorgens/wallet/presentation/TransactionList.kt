package com.amorgens.wallet.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amorgens.NavScreen
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red
import com.amorgens.wallet.domain.Block
import com.amorgens.wallet.domain.Transaction
import java.math.BigDecimal


@Composable
fun  TransactionList(address:String, blocks: List<Block>){
//    val transactions = listOf(
//        Transaction("", "Renuubomi", "300", "34th, may, 2024"),
//        Transaction("", "Lumannie", "4,9900", "3th, Joune, 2024"),
//        Transaction("", "Gerrinu_899", "4,9900", "3th, Joune, 2024")
//    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(blocks){block->
            val transaction = Transaction(block.id,block.receiver_address, block.sender_address,block.amount.toString(), block.date_created)

            TransactionListItem(address, transaction = transaction){}
        }
    }
}

@Composable
fun TransactionListItem(address:String, transaction: Transaction, onclick:()->Unit){
    var amount = BigDecimal("0.0")
    try {
        amount = BigDecimal(transaction.amount)
    }catch (e:Exception){

    }
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                onclick
            }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row (modifier= Modifier
            .fillMaxWidth()
            .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if(transaction.senderAddress == address){transaction.receiverAddress}else{transaction.senderAddress},
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Column (
                horizontalAlignment = Alignment.End
            ){
                Text(text = String.format("%,.2f", amount)+"Kc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if(transaction.senderAddress == address){
                        Red}else{
                        Green}
                )
                Text(text = transaction.dateTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
