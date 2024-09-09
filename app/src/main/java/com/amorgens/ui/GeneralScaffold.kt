package com.amorgens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun GeneralScaffold(topBar: @Composable () -> Unit, floatingActionButton: @Composable ()->Unit, screenView: @Composable ()->Unit) {

    androidx.compose.material3.Scaffold(
        topBar = {
            topBar()
        },
        modifier = Modifier
            .padding(all = 0.dp)
            .background(color = MaterialTheme.colorScheme.secondary),
        bottomBar = {

        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.secondary,
        floatingActionButton = {
            floatingActionButton()
        },

        floatingActionButtonPosition = FabPosition.EndOverlay,
    ) { it ->
        Box(modifier = Modifier.padding(it).padding(start = 15.dp, end = 15.dp)) {
            screenView()
        }
    }
}