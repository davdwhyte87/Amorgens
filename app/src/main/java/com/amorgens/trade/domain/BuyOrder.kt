package com.amorgens.trade.domain

import java.math.BigDecimal

data class BuyOrder(
    val id:String = "",
    val user_name:String = "",
    val amount: BigDecimal = BigDecimal("0.0"),
    val sell_order_id:String = "",
    val is_seller_confirmed:Boolean = false,
    val is_buyer_confirmed:Boolean = false,
    val is_canceled:Boolean = false,
    val is_reported:Boolean = false,
    val created_at:String = "",
    val updated_at:String = ""
)