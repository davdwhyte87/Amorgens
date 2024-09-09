package com.amorgens.trade.domain

data class OrderMessage(
    val id: String  = "",
    val text:String= "" ,
    val image: String= "",
    val created_at:String= "",
    val sender_user_name:String= "",
    val receiver_user_name:String= "",
    val buy_order_id:String= ""
)
