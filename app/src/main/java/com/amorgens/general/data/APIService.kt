package com.amorgens.general.data


import com.amorgens.auth.domain.LoginReq
import com.amorgens.auth.domain.SignupReq
import com.amorgens.feed.domain.CreatePostReq
import com.amorgens.feed.domain.Post
import com.amorgens.general.domain.SystemData
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.OrderMessage
import com.amorgens.trade.domain.PaymentMethodData
import com.amorgens.trade.domain.RequestHeader
import com.amorgens.trade.domain.SellOrder
import com.amorgens.trade.domain.requests.CreateBuyOrderReq
import com.amorgens.trade.domain.requests.CreateOrderMessageReq
import com.amorgens.trade.domain.requests.CreatePaymentMethod
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.response.CancelSellOrderResp
import com.amorgens.trade.domain.response.GenericResp
import com.amorgens.trade.domain.response.MyBuyOrdersResp
import com.amorgens.trade.domain.response.MySellOrdersResponse
import com.amorgens.trade.domain.response.SingleBuyOrdersResp
import com.amorgens.trade.domain.response.SingleSellOrderResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("/davido")
    suspend fun sayHello():Response<String>

    @POST("/api/v1/auth/sell_order/sell")
    suspend fun createSellOrder(@Body data:CreateSellOrderReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<SellOrder>>

    @GET("api/v1/auth/sell_order/my_orders")
    suspend fun getMySellOrders(@HeaderMap header:Map<String,String>):Response<MySellOrdersResponse>

    @GET("api/v1/auth/sell_order/single/{id}")
    suspend fun getSingleSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<SingleSellOrderResp>

    @GET("api/v1/auth/buy_order/single/{id}")
    suspend fun getSingleBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<SingleBuyOrdersResp>
    @GET("api/v1/auth/buy_order/my_orders")
    suspend fun getMyBuyOrders(@HeaderMap header:Map<String,String>):Response<MyBuyOrdersResp>

    @GET("api/v1/auth/sell_order/cancel/{id}")
    suspend fun cancelSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<SellOrder>>

    @GET("api/v1/auth/buy_order/cancel/{id}")
    suspend fun cancelBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<BuyOrder>>

    @GET("api/v1/auth/buy_order/buyer_confirmed/{id}")
    suspend fun buyerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/buy_order/seller_confirmed/{id}")
    suspend fun sellerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/sell_order/open_orders")
    suspend fun getOpenSellOrders(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<SellOrder>>>

    @POST("api/v1/auth/buy_order/buy")
    suspend fun createBuyOrder(@Body order:CreateBuyOrderReq,  @HeaderMap header:Map<String,String> ):Response<GenericResp<BuyOrder>>


    @POST("api/v1/auth/order_message/post")
    suspend fun createOrderMessage(@Body order:CreateOrderMessageReq,  @HeaderMap header:Map<String,String> ):Response<GenericResp<OrderMessage>>

    @GET("api/v1/auth/order_message/get_all/{id}")
    suspend fun getAllOrderMessage(@Path("id") id:String,  @HeaderMap header:Map<String,String> ):Response<GenericResp<List<OrderMessage>>>


    // auth
    @POST("user/kura_signup")
    suspend fun signup(@Body data: SignupReq):Response<GenericResp<String>>

    @POST("user/kura_login")
    suspend fun login(@Body data: LoginReq):Response<GenericResp<String>>


    // payment method
    @GET("api/v1/auth/payment_method/my_payment_methods")
    suspend fun getMyPaymentMethods( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<PaymentMethodData>>>

    @POST("api/v1/auth/payment_method/create")
    suspend fun addPaymentMethods(@Body data:CreatePaymentMethod, @HeaderMap header:Map<String,String> ):Response<GenericResp<PaymentMethodData>>

    @GET("api/v1/auth/payment_method/delete/{id}")
    suspend fun deletePaymentMethod(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>


    // posts --------------------------- ----------------------------- ------------------

    @POST("api/v1/auth/post/create")
    suspend fun createPost(@Body data:CreatePostReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<Post>>

    @GET("api/v1/auth/post/all")
    suspend fun getAllPosts(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<Post>>>


    // system data
    @GET("get_system_data")
    suspend fun getSystemData():Response<GenericResp<SystemData>>

}