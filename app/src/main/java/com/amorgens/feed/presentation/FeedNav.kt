package com.amorgens.feed.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.amorgens.NavScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun feedNav(){
    CenterAlignedTopAppBar(
        title = { Text(text = "", textAlign = TextAlign.Center) },
        navigationIcon = {
            IconButton(onClick = {

            }) {
                Icon(imageVector = Icons.Outlined.Home ,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Menu ,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}