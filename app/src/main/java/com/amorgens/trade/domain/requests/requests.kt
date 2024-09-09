package com.amorgens.trade.domain.requests

import java.math.BigDecimal

data class CreateBuyOrderReq(
    val amount:BigDecimal,
    val sell_order_id:String,
    val wallet_address:String
)

data class CreateOrderMessageReq(
    val receiver_user_name: String,
    val buy_order_id:String,
    val text:String,
    val image:String
)

data class CreatePaymentMethod(
    val payment_method: PaymentMethod = PaymentMethod.Bank,
    val account_name:String ,
    val account_number:String,
    val bank_name:String,
    val other: String = "",
    val paypal_email:String = "",
    val venmo_username:String = "",
    val skrill_email:String = "",
    val name:String
)