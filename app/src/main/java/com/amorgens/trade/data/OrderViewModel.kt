package com.amorgens.trade.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amorgens.general.data.APIService
import com.amorgens.general.data.GetUserToken
import com.amorgens.general.utils.CLog
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.OrderMessage
import com.amorgens.trade.domain.PaymentMethodData
import com.amorgens.trade.domain.SellOrder
import com.amorgens.trade.domain.TradeUIState
import com.amorgens.trade.domain.requests.CreateBuyOrderReq
import com.amorgens.trade.domain.requests.CreateOrderMessageReq
import com.amorgens.trade.domain.requests.CreatePaymentMethod
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import com.amorgens.trade.domain.response.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val apiService: APIService,
    private val application: Application,
    private val getUserToken: GetUserToken
    ) :ViewModel(){

//    val _createSellOrderReq = MutableStateFlow(CreateSellOrderReq(
//        BigDecimal("0.00"),
//        BigDecimal("0.00"),
//        Currency.USD,
//        PaymentMethod.Bank,
//        ""
//    ))
//    val createSellOrderReq = _createSellOrderReq.asStateFlow()

    private val _tradeUIState = MutableStateFlow(TradeUIState())
    val tradeUIState = _tradeUIState.asStateFlow()

    private val _mySellOrders = MutableStateFlow<List<SellOrder>>(emptyList())
    val mySellOrder = _mySellOrders.asStateFlow()

    private val _singleSellOrder = MutableStateFlow(SellOrder())
    val singleSellOrder = _singleSellOrder.asStateFlow()

    private val _myBuyOrders = MutableStateFlow<List<BuyOrder>>(emptyList())
    val myBuyOrders = _myBuyOrders.asStateFlow()

    private val _singleBuyOrder = MutableStateFlow(BuyOrder())
    val singleBuyOrder = _singleBuyOrder.asStateFlow()

    private val _openSellOrders = MutableStateFlow(listOf(SellOrder()))
    val openSellOrders = _openSellOrders.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _buyOrder = MutableStateFlow(BuyOrder())
    val buyOrder = _buyOrder.asStateFlow()

    private val _orderMessages = MutableStateFlow(listOf(OrderMessage()))
    val orderMessages = _orderMessages.asStateFlow()

    private val _paymentMethodDatas = MutableStateFlow(listOf(PaymentMethodData()))
    val paymentMethodDatas = _paymentMethodDatas.asStateFlow()

    fun sayHello(){
        viewModelScope.launch {
            try{
                val resp = apiService.sayHello();
                CLog.error("RESP HELLO XXXX", resp.toString())
            }catch (e: Exception){
                CLog.error("Exception XXXX", e.toString())
            }

        }
    }

    fun clearModelData(){
        _tradeUIState.value = TradeUIState()
        _orderMessages.value = emptyList<OrderMessage>()
        _openSellOrders.value = emptyList<SellOrder>()
        _singleBuyOrder.value = BuyOrder()
        _myBuyOrders.value = emptyList<BuyOrder>()
        _paymentMethodDatas.value = listOf(PaymentMethodData())
        _singleSellOrder.value = SellOrder()
        _mySellOrders.value = listOf(SellOrder())
        _buyOrder.value = BuyOrder()

    }

    fun createSellOrder(data:CreateSellOrderReq){
        _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _tradeUIState.update { it.copy(
                        isCreateSellOrderSuccess = false,
                        isCreateSellOrderError = true,
                        createSellOrderErrorMessage = "You are not authorized"
                    ) }
                    _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
                    return@withContext
                }
                try {

                    val resp = apiService.createSellOrder(data, mapOf("Authorization" to token))
                    if (resp.code() == 200 || resp.code()==201){
                        _tradeUIState.update { it.copy(
                            isCreateSellOrderSuccess = true,
                            isCreateSellOrderError = false) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                            isCreateSellOrderError = true,
                            createSellOrderErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("CREATE ORDER RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _tradeUIState.update {
                            it.copy(isCreateSellOrderSuccess = false,
                            isCreateSellOrderError = true,
                            createSellOrderErrorMessage = errorResp.message
                        ) }
                        CLog.error("CREATE ORDER ERROR", errorResp.server_message+" ")
                    }
                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
                    _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                        isCreateSellOrderError = true, createSellOrderErrorMessage = "Network Error") }

                    CLog.error("CREATE ORDER ERROR", e.toString()+" ")
                }

                _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
            }
        }


    }

    fun addPaymentMethods(createPaymentMethod: CreatePaymentMethod){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _tradeUIState.update { it.copy(
                    isAddPaymentMethodLoading = true,
                )}
                try {
                    val token = getUserToken()
                    val resp = apiService.addPaymentMethods(createPaymentMethod, mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val data =  resp.body()?.data
                        if (data != null){
                            val paymentMethodList = _paymentMethodDatas.value
                            paymentMethodList.toMutableList().add(data)
                            _paymentMethodDatas.value = paymentMethodList
                        }
                        //
                        _tradeUIState.update { it.copy(
                            isAddPaymentMethodLoading = false,
                            isAddPaymentMethodsError = false,
                            isAddPaymentMethodsSuccess = true,
                            addPyamentMethodsErrorMessage = ""
                        ) }

                    }else{

                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)

                        _tradeUIState.update { it.copy(
                            isAddPaymentMethodLoading = false,
                            isAddPaymentMethodsError = true,
                            isAddPaymentMethodsSuccess = false,
                            addPyamentMethodsErrorMessage = errorResp.message
                        ) }

                        CLog.error("ADD PAYMENT METHODS",errorResp.server_message +"" )

                    }
                }catch (e:Exception){

                    _tradeUIState.update { it.copy(
                        isAddPaymentMethodLoading = false,
                        isAddPaymentMethodsError = true,
                        isAddPaymentMethodsSuccess = false,
                        addPyamentMethodsErrorMessage = "Network error"
                    ) }

                    CLog.error("ADD PAYMENT METHODS",e.toString() )
                }

                _tradeUIState.update { it.copy(
                    isAddPaymentMethodLoading = false,
                )}
            }
        }
    }

    fun getAllMyPaymentMethods(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                try {
                    val token = getUserToken()
                    val resp = apiService.getMyPaymentMethods(mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val data =  resp.body()?.data
                        if (data != null){
                            _paymentMethodDatas.value = data
                        }
                        //
                        _tradeUIState.update { it.copy(
                            isGetPaymentMethodLoading = false,
                            isGetPaymentMethodsError = true,
                            isGetPaymentMethodsSuccess = false,
                            getPyamentMethodsErrorMessage = ""
                        ) }

                    }else{

                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)

                        _tradeUIState.update { it.copy(
                            isGetPaymentMethodLoading = false,
                            isGetPaymentMethodsError = true,
                            isGetPaymentMethodsSuccess = false,
                            getPyamentMethodsErrorMessage = errorResp.message
                        ) }

                        CLog.error("GET PAYMENT METHODS",errorResp.server_message +"" )

                    }
                }catch (e:Exception){

                    _tradeUIState.update { it.copy(
                        isGetPaymentMethodLoading = false,
                        isGetPaymentMethodsError = true,
                        isGetPaymentMethodsSuccess = false,
                        getPyamentMethodsErrorMessage = "Network error"
                    ) }

                    CLog.error("GET PAYMENT METHODS",e.toString() )
                }
            }
        }
    }


    fun deletePaymentMethods(id:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _tradeUIState.update { it.copy(
                    isDeletePaymentMethodButtonLoading = true,
                )}
                try {
                    val token = getUserToken()
                    val resp = apiService.deletePaymentMethod(id, mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val data =  resp.body()?.data

                        //
                        _tradeUIState.update { it.copy(
                            isDeletePaymentMethodButtonLoading = false,
                            isDeletePaymentMethodSuccess = true,
                            isDeletePaymentMethodError= false,
                            deletePaymentMethodErrorMessage=""
                        ) }

                    }else{

                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)

                        _tradeUIState.update { it.copy(
                            isDeletePaymentMethodButtonLoading = false,
                            isDeletePaymentMethodSuccess = false,
                            isDeletePaymentMethodError= true,
                            deletePaymentMethodErrorMessage=errorResp.message
                        ) }

                        CLog.error("DELETE PAYMENT METHODS",errorResp.server_message +"" )

                    }
                }catch (e:Exception){

                    _tradeUIState.update { it.copy(
                        isDeletePaymentMethodButtonLoading = false,
                        isDeletePaymentMethodSuccess = false,
                        isDeletePaymentMethodError= true,
                        deletePaymentMethodErrorMessage="Network error"
                    ) }

                    CLog.error("DELETE PAYMENT METHODS",e.toString() )
                }


                _tradeUIState.update { it.copy(
                    isDeletePaymentMethodButtonLoading = true,
                )}
            }
        }
    }

    fun getMySellOrders(){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val token = getUserToken.getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getMySellOrders( mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data
                        if (data.isNullOrEmpty()){
                            _tradeUIState.update {
                                it.copy(
                                    isSuccess = false,
                                    isError = true,
                                    errorMessage = "No Orders"
                                )
                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _mySellOrders.value = data
                        CLog.error("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun getMyBuyOrders(){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val token = sharedPreferences.getString("auth_token", null)
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getMyBuyOrders( mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data
                        if (data.isNullOrEmpty()){
//                            _tradeUIState.update {
//                                it.copy(
//                                    isSuccess = false,
//                                    isError = true,
//                                    errorMessage = "No Orders"
//                                )
//                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _myBuyOrders.value = data
                        CLog.error("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun cancelSellOrder(id:String){
        _tradeUIState.update { it.copy(isCancelSellOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {


                val token = getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                          isCancelSellOrderButtonLoading = false,
                            isCancelSellOrderError = true,
                            isCancelSellOrderSuccess = false,
                            cancelSellOrderErrorMessage = "Unauthorized"
                        )
                    }
                    return@withContext
                }

                try {
                    val resp = apiService.cancelSellOrder(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isCancelSellOrderButtonLoading = false,
                            isCancelSellOrderError = false,
                            isCancelSellOrderSuccess = true,
                            cancelSellOrderErrorMessage = ""
                        ) }

                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isCancelSellOrderButtonLoading = false,
                            isCancelSellOrderError = true,
                            isCancelSellOrderSuccess = false,
                            cancelSellOrderErrorMessage = "Unauthorized"
                        ) }
                    }
                    else{
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        CLog.error("CANCEL SELL ORDER ERROR", resp.errorBody()?.string()+"")
                        _tradeUIState.update { it.copy(
                            isCancelSellOrderButtonLoading = false,
                            isCancelSellOrderError = true,
                            isCancelSellOrderSuccess = false,
                            cancelSellOrderErrorMessage = errorResp.message
                        ) }

                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isCancelSellOrderButtonLoading = false,
                        isCancelSellOrderError = true,
                        isCancelSellOrderSuccess = false,
                        cancelSellOrderErrorMessage = "Network Error"
                    ) }
                    CLog.error("CANCEL SELL ORDER ERROR", e.toString()+"")
                }
                _tradeUIState.update { it.copy(isCancelBuyOrderButtonLoading = false) }
            }
        }
    }



    fun cancelBuyOrder(id:String){
        _tradeUIState.update { it.copy(isCancelBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val token = getUserToken.getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isCancelBuyOrderSuccess = false,
                            isCancelBuyOrderError = true,
                            cancelBuyOrderError = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isCancelBuyOrderButtonLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.cancelBuyOrder(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isCancelBuyOrderSuccess = true,
                            isCancelBuyOrderError = false,
                            cancelBuyOrderError = ""
                        ) }

                        CLog.error("CANCEL BUY ORDER XXXX", resp.body().toString())
                    }else if (resp.code()==401){
                        CLog.error("CANCEL BUY ORDER", " Unauthorized")
                        _tradeUIState.update { it.copy(
                            isCancelBuyOrderSuccess = false,
                            isCancelBuyOrderError = true,
                            cancelBuyOrderError = "You are not authorized"
                        ) }
                    }
                    else{
                        val respB = resp.errorBody()?.string()
                        CLog.error("CANCEL BUY ORDER ERROR", respB+"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<BuyOrder>>() {}.type
                        val errorResp: GenericResp<BuyOrder> = gson.fromJson(respB, genericType)

                        _tradeUIState.update { it.copy(
                            isCancelBuyOrderSuccess = false,
                            isCancelBuyOrderError = true,
                            cancelBuyOrderError = errorResp.message

                        ) }

                    }

                }catch (e:Exception){

                    CLog.error("CANCEL BUY ORDER ERROR", e.toString())
                    _tradeUIState.update { it.copy(
                        isCancelBuyOrderSuccess = false,
                        isCancelBuyOrderError = true,
                        cancelBuyOrderError = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isCancelBuyOrderButtonLoading = false) }
                }

                _tradeUIState.update { it.copy(isCancelBuyOrderButtonLoading = false) }
            }
        }
    }

    fun getSingleSellOrders(id:String){
        _tradeUIState.update { it.copy(isGetSingleSellOrderPageLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {


                val token =getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                           isGetSingleSellOrderPageLoading = false,
                            isGetSingleSellOrderPageError = true,
                            isGetSingleSellOrderPageSuccess = false,
                            getSingleSellOrderPageErrorMessage = "Unauthorized"
                        )
                    }
                    return@withContext
                }

                try {
                    val resp = apiService.getSingleSellOrder(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data ?: SellOrder()
                        if (data.created_at.isBlank()){
//                            _tradeUIState.update {
//                                it.copy(
//                                    isGetSingleSellOrderPageLoading = false,
//                                    isGetSingleSellOrderPageError = true,
//                                    isGetSingleSellOrderPageSuccess = false,
//                                    getSingleSellOrderPageErrorMessage = ""
//                                )
//                            }

                            return@withContext
                        }
                        _singleSellOrder.value = data
                        CLog.error("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isGetSingleSellOrderPageLoading = false,
                            isGetSingleSellOrderPageError = true,
                            isGetSingleSellOrderPageSuccess = false,
                            getSingleSellOrderPageErrorMessage = "Unauthorized"
                        ) }
                    }
                    else{
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<SellOrder>>() {}.type
                        val errorResp: GenericResp<SellOrder> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _tradeUIState.update { it.copy(
                            isGetSingleSellOrderPageLoading = false,
                            isGetSingleSellOrderPageError = true,
                            isGetSingleSellOrderPageSuccess = false,
                            getSingleSellOrderPageErrorMessage = errorResp.message
                        ) }
                        CLog.error("SINGLE ORDER ERROR XXXX", errorResp.server_message+"")
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isGetSingleSellOrderPageLoading = false,
                        isGetSingleSellOrderPageError = true,
                        isGetSingleSellOrderPageSuccess = false,
                        getSingleSellOrderPageErrorMessage = "Network Error"
                    ) }

                    CLog.error("SINGLE ORDER ERROR XXXX", e.toString())
                }
                _tradeUIState.update { it.copy(isGetSingleSellOrderPageLoading = false) }
            }
        }
    }

    fun resetSuccessAndError(){
        _tradeUIState.update { it.copy(
            isSuccess = false,
            isError = false,
            errorMessage = "",
            successMessage = ""
        ) }
    }

    fun resetCreateSellOrderScreen(){
        _tradeUIState.update {
            it.copy(
                isCreateSellOrderSuccess = false,
                isCreateSellOrderError = false,
                createSellOrderErrorMessage = "",
                isCreateSellOrderButtonLoading = false
            )
        }
    }

    fun resetAddPaymentMethodScreen(){
        _tradeUIState.update { it.copy(
            isAddPaymentMethodLoading = false,
            isAddPaymentMethodsSuccess = false,
            isAddPaymentMethodsError = false,
            addPyamentMethodsErrorMessage = ""
        ) }
    }

    fun resetGetPaymentMethodScreen(){
        _tradeUIState.update { it.copy(
            isGetPaymentMethodLoading = false,
            isGetPaymentMethodsSuccess = false,
            isGetPaymentMethodsError = false,
            getPyamentMethodsErrorMessage = "",

            isDeletePaymentMethodSuccess = false,
            isDeletePaymentMethodButtonLoading = false,
            isDeletePaymentMethodError = false,
            deletePaymentMethodErrorMessage = ""
        ) }
    }

    fun resetCreateBuyOrderScreen(){
        _tradeUIState.update {
            it.copy(
                isCreateBuyOrderButtonLoading = false,
                isCreateBuyOrderSuccess = false,
                isCreateBuyOrderError = false,
                createBuyOrderErrorMessage = ""
            )
        }
    }

    fun resetSingleOrderScreenState(){
        _tradeUIState.update {
            it.copy(
                isConfirmBuyOrderError = false,
                isConfirmBuyOrderSuccess = false,
                confirmBuyOrderErrorMessage = "",
                isConfirmBuyOrderButtonLoading = false,
                isCancelBuyOrderButtonLoading = false,
                isCancelBuyOrderError = false,
                isCancelBuyOrderSuccess = false
            )
        }
    }


    fun resetSuccessAndErrorCreateSellOrder(){
        _tradeUIState.update { it.copy(
            isCreateSellOrderSuccess = false,
            isCreateSellOrderError = false,
            createSellOrderErrorMessage = ""
        ) }
    }

    fun getSingleBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isGetSingleBuyOrderPageLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val token = getUserToken.getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isGetSingleBuyOrderPageSuccess = false,
                            isGetSingleBuyOrderPageError = true,
                            getSingleBuyOrderPageErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isGetSingleBuyOrderPageLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getSingleBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){

                        val data = resp.body()?.data
                        if (data == null){
                            _tradeUIState.update {
                                it.copy(
                                    isGetSingleBuyOrderPageSuccess = false,
                                    isGetSingleBuyOrderPageError = true,
                                    getSingleBuyOrderPageErrorMessage = "Not found"
                                )
                            }
                            _tradeUIState.update { it.copy(isGetSingleBuyOrderPageLoading = false) }
                            return@withContext
                        }
                        _tradeUIState.update { it.copy(
                            isGetSingleBuyOrderPageSuccess = true,
                            isGetSingleBuyOrderPageError = false,
                            getSingleBuyOrderPageErrorMessage = ""
                        ) }
                        _singleBuyOrder.value = data

                        // get the sell order too
                        getSingleSellOrders(_singleBuyOrder.value.sell_order_id)
                        CLog.error("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isGetSingleBuyOrderPageSuccess = false,
                            isGetSingleBuyOrderPageError = true,
                            getSingleBuyOrderPageErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        val  respData =  resp.errorBody()?.string()
                        CLog.error("SIGNUP ERROR !", respData+"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respData, genericType)
                        _tradeUIState.update { it.copy(
                            isGetSingleBuyOrderPageSuccess = false,
                            isGetSingleBuyOrderPageError = true,
                            getSingleBuyOrderPageErrorMessage = errorResp.message

                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isGetSingleBuyOrderPageSuccess = false,
                        isGetSingleBuyOrderPageError = true,
                        getSingleBuyOrderPageErrorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isGetSingleBuyOrderPageLoading = false) }
                }
                _tradeUIState.update { it.copy(isGetSingleBuyOrderPageLoading = false) }
            }
        }
    }


    fun buyerConfirmBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val token = sharedPreferences.getString("auth_token", null)
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage =  "Unauthorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.buyerConfirmBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = true,
                            isConfirmBuyOrderError = false,
                            confirmBuyOrderErrorMessage =  ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"

                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = resp.body()?.message ?:""

                        ) }
                        CLog.error("XXX CONFIRM ORDER ERROR ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isConfirmBuyOrderSuccess = false,
                        isConfirmBuyOrderError = true,
                        confirmBuyOrderErrorMessage =  "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
                    CLog.error("XXX CONFIRM ORDER ERROR ", e.toString())
                }
                _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
            }

        }
    }

    fun sellerConfirmBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val token = sharedPreferences.getString("auth_token", null)
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.sellerConfirmBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = true,
                            isConfirmBuyOrderError = false,
                            confirmBuyOrderErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = resp.body()?.message ?: ""

                        ) }
                        CLog.error("XXX CONFIRM ORDER ERROR ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isConfirmBuyOrderSuccess = false,
                        isConfirmBuyOrderError = true,
                        confirmBuyOrderErrorMessage =  "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
                    CLog.error("XXX CONFIRM ORDER ERROR ", e.toString())
                }
                _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
            }

        }
    }

    fun getOpenOrders(){
        _tradeUIState.update { it.copy(isGetOpenOrdersPageLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val token = sharedPreferences.getString("auth_token", null)
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isGetOpenOrdersPageSuccess = false,
                            isGetOpenOrdersPageError = true,
                            getOpenOrdersPageErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isGetOpenOrdersPageLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.getOpenSellOrders(mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if (data==null){
                            _openSellOrders.value = openSellOrders.value
                        }else{
                            _openSellOrders.value = data
                        }
                        _tradeUIState.update { it.copy(
                            isGetOpenOrdersPageSuccess = true,
                            isGetOpenOrdersPageError = false,
                            getOpenOrdersPageErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isGetOpenOrdersPageSuccess = false,
                            isGetOpenOrdersPageError = true,
                            getOpenOrdersPageErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isGetOpenOrdersPageSuccess = false,
                            isGetOpenOrdersPageError = true,
                            getOpenOrdersPageErrorMessage = resp.body()?.message ?: ""


                        ) }
                        CLog.error("XXX GET OPEN ORDER ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isGetOpenOrdersPageSuccess = false,
                        isGetOpenOrdersPageError = true,
                        getOpenOrdersPageErrorMessage = "Network error"
                    ) }
                    _tradeUIState.update { it.copy(
                        isGetOpenOrdersPageLoading = false) }
                    CLog.error("XXX GET OPEN ORDERS ", e.toString())
                }
                _tradeUIState.update { it.copy(isGetOpenOrdersPageLoading = false) }
            }

        }
    }


    fun createBuyOrder(buyOrderReq:CreateBuyOrderReq){
        _tradeUIState.update { it.copy(isCreateBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {


                val token = getUserToken.getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isCreateBuyOrderSuccess = false,
                            isCreateBuyOrderError = true,
                            createBuyOrderErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isCreateBuyOrderButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.createBuyOrder(buyOrderReq, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if (data==null){
                            _tradeUIState.update { it.copy(
                                isCreateBuyOrderSuccess = false,
                                isCreateBuyOrderError = true,
                                createBuyOrderErrorMessage = "Not found"
                            ) }
                            return@withContext
//                            _openSellOrders.value = openSellOrders.value
                        }else{
                            _buyOrder.value = data
                        }
                        _tradeUIState.update { it.copy(
                            isCreateBuyOrderSuccess = true,
                            isCreateBuyOrderError = false,
                            createBuyOrderErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isCreateBuyOrderSuccess = false,
                            isCreateBuyOrderError = true,
                            createBuyOrderErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        val respData = resp.errorBody().toString()
                        CLog.error("CREATE BUY ORDER ERROR ", respData)
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respData , genericType)
                        _tradeUIState.update { it.copy(
                            isCreateBuyOrderSuccess = false,
                            isCreateBuyOrderError = true,
                            createBuyOrderErrorMessage = respData ?: ""


                        ) }
                        CLog.error("XXX CREATE BUY ORDER ERROR", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isCreateBuyOrderSuccess = false,
                        isCreateBuyOrderError = true,
                        createBuyOrderErrorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(
                        isCreateBuyOrderButtonLoading = false) }
                    CLog.error("XXX CREATE BUY ORDER ", e.toString())
                }
                _tradeUIState.update { it.copy(isCreateBuyOrderButtonLoading = false) }
            }

        }
    }


    fun getUserToken():String{
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val token = sharedPreferences.getString("auth_token", null)
        return token ?: ""
    }
    fun getOrderMessages(id:String){
        _tradeUIState.update { it.copy(isGetAllOrderMessagesLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val token = getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isGetAllOrderMessagesError = true,
                            isGetAllOrderMessagesSuccess = false,
                            getAllOrderMessagesErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isGetAllOrderMessagesLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.getAllOrderMessage(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if (data==null){
                            _tradeUIState.update { it.copy(
                                isGetAllOrderMessagesError = true,
                                isGetAllOrderMessagesSuccess = false,
                                getAllOrderMessagesErrorMessage = "Not found"
                            ) }
                            return@withContext
//                            _openSellOrders.value = openSellOrders.value
                        }else{
                            _orderMessages.value = data
                        }
                        _tradeUIState.update { it.copy(
                            isGetAllOrderMessagesError = false,
                            isGetAllOrderMessagesSuccess = true,
                            getAllOrderMessagesErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isGetAllOrderMessagesError = true,
                            isGetAllOrderMessagesSuccess = false,
                            getAllOrderMessagesErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isGetAllOrderMessagesError = true,
                            isGetAllOrderMessagesSuccess = false,
                            getAllOrderMessagesErrorMessage = resp.body()?.message ?: ""
                        ) }
                        CLog.error("XXX CREATE ORDER MESSAGE ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isGetAllOrderMessagesError = true,
                        isGetAllOrderMessagesSuccess = false,
                        getAllOrderMessagesErrorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(
                        isGetAllOrderMessagesLoading = false) }
                    CLog.error("XXX CREATE MESSAGE ORDER", e.toString())
                }
                _tradeUIState.update { it.copy(isGetAllOrderMessagesLoading = false) }
            }

        }
    }

    fun createOrderMessage(orderMessage: CreateOrderMessageReq){
        _tradeUIState.update { it.copy(isCreateOrderMessageButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val token = getUserToken()
                if (token.isBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isCreateOrderMessagesError = true,
                            isCreateOrderMessageSuccess = false,
                            createOrderMessageErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isCreateOrderMessageButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.createOrderMessage(orderMessage, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if (data==null) {
//                            _tradeUIState.update {
//                                it.copy(
//                                    isCreateOrderMessageSuccess = false ,
//                                    isCreateOrderMessagesError = true,
//                                    createOrderMessageErrorMessage = ""
//                                )
//                            }
                            return@withContext
                        }
                        val  listMessages = _orderMessages.value.toMutableList()
                        listMessages.add(data)
                        _orderMessages.value = listMessages

                        _tradeUIState.update { it.copy(
                            isCreateOrderMessagesError = false,
                            isCreateOrderMessageSuccess = true,
                            createOrderMessageErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isCreateOrderMessagesError = true,
                            isCreateOrderMessageSuccess = false,
                            createOrderMessageErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isCreateOrderMessagesError = true,
                            isCreateOrderMessageSuccess = false,
                            createOrderMessageErrorMessage = resp.body()?.message ?: ""
                        ) }
                        CLog.error("XXX CREATE ORDER MESSAGE", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isCreateOrderMessagesError = true,
                        isCreateOrderMessageSuccess = false,
                        createOrderMessageErrorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(
                        isCreateOrderMessageButtonLoading = false) }
                    CLog.error("XXX CREATE MESSAGE ORDER", e.toString())
                }
                _tradeUIState.update { it.copy(isCreateOrderMessageButtonLoading = false) }
            }

        }
    }
//    fun login(){
//        val context = application
//        val mshared = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        val mSharedEditor = mshared.edit()
//        mSharedEditor.putString("user_name", "roland_case")
//        mSharedEditor.apply()
//
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
//
//        val sharedPreferences = EncryptedSharedPreferences.create(
//            context,
//            "secure_prefs",
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//
//        val editor = sharedPreferences.edit()
//        editor.putString("auth_token",
//            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVXNlciIsImVtYWlsIjoicm9sYW5kQHguY29tIiwidXNlcl9uYW1lIjoicm9sYW5kX2Nhc2UiLCJleHAiOjE3NTUxNjIwMzl9.Pj6q31lTelU43ll8JlX9qH_ZeoSvTz4e2_-WmQDuyF8")
//        editor.apply()
//    }

    // get the current excahnge rate
    fun getExchangeValue(amount:BigDecimal):BigDecimal{
        val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
        try {
            val rate = BigDecimal( sharedPreferences.getString("NGN", "0.5"))
            return rate.multiply(amount)
        }catch (e:Exception){
            return BigDecimal("0.00")
        }
    }

    // set excahnge rate
    fun setExchangeRate(key:String, value:String){
        val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString(key, value).apply()
    }

    fun resetSingleSellOrderScreenUI(){
        _tradeUIState.update { it.copy(
            isCancelSellOrderSuccess = false,
            isCancelSellOrderButtonLoading = false,
            isCancelSellOrderError = false,
            cancelSellOrderErrorMessage = ""
        ) }
    }

    fun getUserName(){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }

}