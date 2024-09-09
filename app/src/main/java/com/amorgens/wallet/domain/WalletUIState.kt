package com.amorgens.wallet.domain

data class WalletUIState (
    val isCreateWalletButtonLoading:Boolean = false,
    val isCreateWalletDone: Boolean = false,
    val createWalletScreenNavigateBack:Boolean = false,
    val createWalletSuccess:Boolean = false,
    val createWalletSuccessMessage: String ="",
    val createWalletError: Boolean = false,
    val createWalletErrorMessage:String ="",
    val isError:Boolean = false,
    val isSuccess:Boolean = false,
    val errorMessage:String ="",
    val successMessage:String ="",

    val isAddWalletDone:Boolean = false,
    val isSingleWalletPageLoading: Boolean = false,
    val isSyncingLocalWallet:Boolean = false,

    val isAddWalletButtonLoading:Boolean = false,
    val isAddWalletSuccess:Boolean = false,
    val isAddWalletError:Boolean = false,
    val addWalletErrorMessage:String="",

    val isTransferSuccessful:Boolean = false,
    val isTransferError:Boolean = false,
    val isTransferButtonLoading:Boolean = false,
    val transferErrorMessage:String = " "
)