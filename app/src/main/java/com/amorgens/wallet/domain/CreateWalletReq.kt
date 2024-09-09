package com.amorgens.wallet.domain

data class CreateWalletReq (
    val address:String,
    val password:String,
    val wallet_name:String,
    val vcid_username:String,
    val is_vcid:Boolean
)