package com.amorgens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun currencyDropDown(title:String){
    val isExpanded = remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .padding(1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Box(
            modifier = Modifier.clickable(onClick = {
                if (isExpanded.value) isExpanded.value =
                    false else isExpanded.value = true
            })
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = ""
                )
                Text(
                    text = "NGN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface
                )

            }
        }
        DropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }) {
            DropdownMenuItem(text = { Text(text = "NGN") }, onClick = {

            })

        }
    }
}