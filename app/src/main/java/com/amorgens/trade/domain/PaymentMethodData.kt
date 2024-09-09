package com.amorgens.trade.domain

import com.amorgens.trade.domain.requests.PaymentMethod

data class PaymentMethodData(
    val id:String = "",
    val user_name:String = "",
    val payment_method:PaymentMethod = PaymentMethod.Bank,
    val account_name:String = "",
    val account_number:String = "",
    val bank_name:String= "",
    val other:String = "",
    val paypal_email:String = "",
    val venmo_username: String = "",
    val skrill_email:String =  "",
    val name:String = ""
)
