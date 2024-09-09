package com.amorgens.wallet.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal


@Entity(tableName = "wallet")
data class Wallet(
    val id: String ="",
    val walletName:String= "",
    @PrimaryKey(autoGenerate = false)
    val walletAddress:String ="",
    val balance:BigDecimal =BigDecimal("0.0"),
    val userName:String? = null
)