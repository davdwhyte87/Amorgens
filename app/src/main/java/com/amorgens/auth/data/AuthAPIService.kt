package com.amorgens.auth.data

import com.amorgens.auth.domain.SignupReq
import com.amorgens.trade.domain.response.GenericResp
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPIService {

    @POST("user/kura_signup")
    suspend fun signup(@Body data:SignupReq):GenericResp<String>


}