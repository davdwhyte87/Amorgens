package com.amorgens.wallet.domain

data class TransferReq(
    val sender:String,
    val receiver:String,
    val amount:String,
    val transaction_id:String,
    val sender_password:String
)