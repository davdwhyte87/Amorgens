package com.amorgens.wallet.domain

data class Transaction(
    val id:String,
    val receiverAddress:String,
    val senderAddress:String,
    var amount: String,
    var dateTime:String,

    )