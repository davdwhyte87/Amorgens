package com.amorgens.wallet.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amorgens.general.utils.CLog
import com.amorgens.wallet.domain.Block
import com.amorgens.wallet.domain.Chain
import com.amorgens.wallet.domain.CreateWalletReq
import com.amorgens.wallet.domain.GetBalanceReq
import com.amorgens.wallet.domain.GetWalletReq
import com.amorgens.wallet.domain.TransferReq
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.WalletC
import com.amorgens.wallet.domain.WalletUIState
import com.amorgens.wallet.presentation.formatter
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletService: WalletService,
    private val walletRepository: WalletRepository,
    private val application: Application
) : ViewModel(){
    val defaultSingleWallet = Wallet()

    private val _singleWallet = MutableStateFlow(defaultSingleWallet)
    val singleWallet = _singleWallet.asStateFlow()

    private val _singleWalletC = MutableStateFlow(WalletC())
    val singleWalletC = _singleWalletC.asStateFlow()

    private val _allWallets = MutableStateFlow(listOf(defaultSingleWallet))
    val allWallets = _allWallets.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    val defaultUIState = WalletUIState()
    val _walletUIState = MutableStateFlow(defaultUIState)
    val walletUIState = _walletUIState.asStateFlow()



    fun clearModelData(){
        _walletUIState.value = WalletUIState()
        _singleWalletC.value = WalletC()
        _allWallets.value = emptyList()
        _singleWallet.value = Wallet()

    }


    fun updateSingleWallet(wallet:Wallet){
        _singleWallet.value = wallet
    }
    fun createWallet(createWalletRequest:CreateWalletReq){
        val gson= Gson()
        val reqString = gson.toJson(createWalletRequest)
        val message = formatter("CreateWallet", reqString)

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val resp = walletService.sendData(message)
                    CLog.error("RESPDATA XXXX", resp)
                    // seperate response string
                    val respPack = resp.split("\n")
                    if(respPack.get(0) == "1"){
                        // success
                        _walletUIState.update { it.copy(createWalletSuccess = true, createWalletSuccessMessage = respPack.get(1)) }
                        _walletUIState.update { it.copy(createWalletError = false, createWalletErrorMessage = "" )}
                        // add wallet locally
                        // save wallet data to database
                        val wallet = Wallet("",createWalletRequest.wallet_name,createWalletRequest.address, BigDecimal("0.0"), getUserName(application))
                        walletRepository.insertWallet(wallet)

                    }else{
                        _walletUIState.update { it.copy(createWalletError = true, createWalletErrorMessage = respPack.get(1)) }
                        clearSuccess()
                        _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "" )}
                    }
                }catch (exception:IOException){
                    println("ERROR XXXX"+ exception.toString())
                    CLog.error("ERROR XXXX", exception.toString())
                    _walletUIState.update { it.copy(createWalletError = true, createWalletErrorMessage = exception.toString()) }
                    clearSuccess()
                    _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "" )}
                }
            }
            updateIsCreateWalletButtonLoading(false)
            updateCreateWalletScreenNavigateBack(true)
        }


    }

    fun getBalanceRemote(getWalletReq:GetBalanceReq){
        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetBalance", reqString)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    CLog.error("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        // success
                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }

                        // save wallet data to database
                        val wallet = Wallet(UUID.randomUUID().toString(),getWalletReq.address, getWalletReq.address,BigDecimal(respPack[2]),getUserName(application) )
                        walletRepository.insertWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)

                    }else{
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                    }
                }catch (exception:Exception){
                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }

                }

            }

            updateIsAddWalletButtonLoading(false)

        }
    }

    fun getUserName(application:Context):String{
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
        return  _userName.value
    }

    fun addWallet(getWalletReq:GetWalletReq){
        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetWalletData", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _walletUIState.update { it.copy(isAddWalletButtonLoading = true) }
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    CLog.error("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        // success

//                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
//                        clearError()
                        // convert response data to object
                        val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                        CLog.error("Converted OBJ XXX", walletC.toString())

                        // update single wallet variable
                        //_singleWalletC.value = walletC
                        // trigger back navigation
                        // save wallet data to database
                        val wallet = Wallet(walletC.id,walletC.address, getWalletReq.address, walletC.chain.chain.last().balance, getUserName(application))
                        walletRepository.insertWallet(wallet)

                        // update ui state
                        _walletUIState.update { it.copy(
                            isAddWalletButtonLoading = false,
                            isAddWalletSuccess = true,
                            isAddWalletError = false,
                            isAddWalletDone = true,
                            addWalletErrorMessage = ""
                        ) }
                    }else{

                        // error
                        _walletUIState.update { it.copy(
                            isAddWalletButtonLoading = false,
                            isAddWalletSuccess = false,
                            isAddWalletError = true,
                            isAddWalletDone = false,
                            addWalletErrorMessage =  respPack.get(1)
                        ) }
                        CLog.error("GET WALLET ERROR ", respPack.get(1) )

                    }
                }catch (exception:Exception){
                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(
                        isAddWalletButtonLoading = false,
                        isAddWalletSuccess = false,
                        isAddWalletError = true,
                        isAddWalletDone = false,
                        addWalletErrorMessage =  exception.toString()
                    ) }
                }
            }
        }
    }

    fun getWalletRemote(getWalletReq:GetWalletReq){
        // change loading state
        _walletUIState.update { it.copy(isSingleWalletPageLoading = true) }

        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetWalletData", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    CLog.error("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                        // success
                        //_walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                        // clear error
                        clearError()
                        // convert response data to object
                        val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                        CLog.error("Converted OBJ XXX", walletC.toString())

                        // update single wallet variable
                        _singleWalletC.value = walletC


                        // update wallet data locally
                        val wallet = Wallet(walletC.id,walletC.address, getWalletReq.address, walletC.chain.chain.last().balance, getUserName(application))
                        walletRepository.updateWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)


                    }else{
                        _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                        clearSuccess()
                    }
                }catch (exception:Exception){
                    _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }
                    clearSuccess()
                }
            }
        }
    }

    fun transfer(transferReq: TransferReq){
        // change loading state
        _walletUIState.update { it.copy(isTransferButtonLoading = true) }

        val gson= Gson()
        val reqString = gson.toJson(transferReq)
        val message = formatter("Transfer", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    CLog.error("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        _walletUIState.update { it.copy(isTransferButtonLoading = false) }
                        // success
                        _walletUIState.update {
                            it.copy(
                                isTransferSuccessful = true,
                                isTransferError = false,
                                isTransferButtonLoading = false,
                                transferErrorMessage = ""
                            )
                        }

                        // trigger back navigation

                    }else{
                        _walletUIState.update {
                            it.copy(
                                isTransferSuccessful = false,
                                isTransferError = true,
                                isTransferButtonLoading = false,
                                transferErrorMessage = respPack[2]
                            )
                        }
                    }
                }catch (exception:Exception){

                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update {
                        it.copy(
                            isTransferSuccessful = false,
                            isTransferError = true,
                            isTransferButtonLoading = false,
                            transferErrorMessage = exception.toString()
                        )
                    }
                }
            }
        }
    }


    // run through a list of users wallets and get the wallet address data remotely
    fun updateWalletsLocal(addresses: List<String>){
        _walletUIState.update { it.copy(isSyncingLocalWallet = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                addresses.forEachIndexed { index, address ->
                    val gson= Gson()
                    val reqString = gson.toJson(GetWalletReq(address))
                    val message = formatter("GetWalletData", reqString)
                    try{
                        val resp = walletService.sendData(message)
                        val respPack = resp.split("\n")
                        CLog.error("XXX RESPONSE", resp)
                        if(respPack[0] == "1"){

                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                            // success
                            //_walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                            // clear error
                            clearError()

                            // convert response data to object
                            val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                            CLog.error("Converted OBJ XXX", walletC.toString())

                            // update wallet data locally
                            val wallet = Wallet(walletC.id,walletC.address, address, walletC.chain.chain.last().balance, getUserName(application))
                            walletRepository.updateWallet(wallet)

                        }else{
                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }

                            // error
                            _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                            clearSuccess()
                        }
                    }catch (exception:Exception) {
                        _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                        CLog.error("XXX Yola", exception.toString())
                        _walletUIState.update {
                            it.copy(
                                isError = true,
                                errorMessage = "Network Error"
                            )
                        }
                        clearSuccess()
                    }

                }
            }
        }
    }


    // get all wallets in local db for the logged in user
    fun getAllWallets(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    _allWallets.value= walletRepository.getAllWallets2(getUserName(application)).first()
                }catch (e:Exception){

                    CLog.error("GET ALL WALLETS ERROR", e.toString())
                }

            }
        }
    }

    // amara101_sigil
    fun updateUIStateData(data:WalletUIState){
        _walletUIState.value = data
    }

    fun updateIsCreateWalletButtonLoading(data:Boolean){
        _walletUIState.update { it.copy(isCreateWalletButtonLoading = data) }
    }
    fun updateIsAddWalletButtonLoading(data:Boolean){
        _walletUIState.update { it.copy(isAddWalletButtonLoading = data)}
    }
    fun updateCreateWalletScreenNavigateBack(data:Boolean){
        _walletUIState.update { it.copy(createWalletScreenNavigateBack = data) }
    }

    fun updateIsAddWalletDone(data:Boolean){
        _walletUIState.update { it.copy(isAddWalletDone = data) }
    }

    fun resetUIState(){
        CLog.error("RESET UI XXXXX","yes")
        _walletUIState.value =  defaultUIState
    }

    fun clearCreateWalletSuccessData(){
        _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "") }
    }
    fun clearCreateWalletErrorData(){
        _walletUIState.update { it.copy(createWalletError = false, createWalletErrorMessage = "") }
    }

    fun clearSuccess(){
        _walletUIState.update { it.copy(isSuccess = false, successMessage = "") }
    }
    fun clearError(){
        _walletUIState.update { it.copy(isError = false, errorMessage = "") }
    }


}





