package com.amorgens.trade.domain

import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod

import java.math.BigDecimal

//data class SellOrder(
//    val id:String,
//    val userName:String,
//    val minAmount:BigDecimal,
//    val maxAmount:BigDecimal,
//    val amount:BigDecimal
//)

data class SellOrder(
    val id:String = "",
    val user_name:String = "",
    val buy_orders_id:List<String> = listOf(""),
    val buy_orders:List<BuyOrder> = listOf(BuyOrder()),
    val amount: BigDecimal = BigDecimal("0.0"),
    val min_amount:BigDecimal= BigDecimal("0.00"),
    val max_amount:BigDecimal= BigDecimal("0.00"),
    val is_closed:Boolean= false,
    val currency: Currency = Currency.NGN,
    val created_at:String = "",
    val updated_at:String= "",
    val payment_method: PaymentMethod= PaymentMethod.Bank,
    val payment_method_id:String= "",
    val payment_method_data: PaymentMethodData? = PaymentMethodData(),
    val wallet_address:String = ""
)