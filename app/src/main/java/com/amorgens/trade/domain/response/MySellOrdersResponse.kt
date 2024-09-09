package com.amorgens.trade.domain.response

import com.amorgens.trade.domain.SellOrder
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import java.math.BigDecimal

data class MySellOrdersResponse(
    val message:String ,
    val data: List<SellOrder>
)



