package com.amorgens.menu.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemData(
    val title:String,
    val icon: ImageVector,
    val route: String
)