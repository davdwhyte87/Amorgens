package com.amorgens.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.sharp.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(pageName:String, navController: NavController){
    CenterAlignedTopAppBar(
        title = { Text(text = pageName, textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Sharp.ArrowBackIosNew ,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}