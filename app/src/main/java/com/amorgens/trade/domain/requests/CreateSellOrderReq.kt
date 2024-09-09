package com.amorgens.trade.domain.requests

import java.math.BigDecimal

data class CreateSellOrderReq(
    val amount:BigDecimal,
    val min_amount:BigDecimal,
    //val max_amount:BigDecimal,
    val currency:Currency,
    val payment_method:PaymentMethod,
    val payment_method_id:String,
    val wallet_address:String,
    val phone_number:String,
    val password:String
)

enum class Currency{
    NGN,
    USD
}

enum class PaymentMethod {
    Bank,
    Paypal
}

