package com.amorgens.wallet.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun WalletMenuItem(iconImage:ImageVector, menuTitle:String, onClick:()->Unit){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(10.dp)
            .size(width = 80.dp, height = 80.dp),
        onClick = {
            onClick()
        }
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,

            ){
            Icon(imageVector = iconImage,
                contentDescription = menuTitle,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Text(text = menuTitle)
        }
    }
}