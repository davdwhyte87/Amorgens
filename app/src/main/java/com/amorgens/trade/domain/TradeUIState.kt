package com.amorgens.trade.domain

data class TradeUIState(
    val isCreateSellOrderSuccess:Boolean = false,
    val isCreateSellOrderError:Boolean = false,
    val isCreateSellOrderButtonLoading:Boolean = false,
    val createSellOrderErrorMessage:String ="",
    val isError:Boolean = false,
    val isSuccess:Boolean = false,
    val errorMessage:String = "",
    val successMessage:String = "",
    val isLoading:Boolean = false,

    val isGetSingleBuyOrderPageLoading:Boolean = false,
    val isGetSingleBuyOrderPageError:Boolean = false,
    val isGetSingleBuyOrderPageSuccess:Boolean = false,
    val getSingleBuyOrderPageErrorMessage:String = "",

    val isGetSingleSellOrderPageLoading:Boolean = false,
    val isGetSingleSellOrderPageError:Boolean = false,
    val isGetSingleSellOrderPageSuccess:Boolean = false,
    val getSingleSellOrderPageErrorMessage:String = "",

    val isCancelSellOrderButtonLoading: Boolean = false,
    val isCancelSellOrderSuccess:Boolean = false,
    val isCancelSellOrderError:Boolean = false,
    val cancelSellOrderErrorMessage:String = "",



    val isConfirmBuyOrderButtonLoading:Boolean = false,
    val isConfirmBuyOrderSuccess: Boolean = false,
    val isConfirmBuyOrderError: Boolean = false,
    val confirmBuyOrderErrorMessage:String = "",

    val isGetOpenOrdersPageLoading: Boolean = false,
    val isGetOpenOrdersPageSuccess:Boolean = false,
    val isGetOpenOrdersPageError:Boolean = false,
    val getOpenOrdersPageErrorMessage: String = "",

    val isCreateBuyOrderButtonLoading: Boolean = false,
    val isCreateBuyOrderSuccess:Boolean = false,
    val isCreateBuyOrderError:Boolean = false,
    val createBuyOrderErrorMessage:String = "",


    val isCancelBuyOrderButtonLoading:Boolean = false,
    val isCancelBuyOrderError:Boolean= false,
    val isCancelBuyOrderSuccess:Boolean = false,
    val cancelBuyOrderError:String = "",

    val isGetAllOrderMessagesLoading:Boolean = false,
    val isGetAllOrderMessagesSuccess:Boolean = false,
    val isGetAllOrderMessagesError:Boolean= false,
    val getAllOrderMessagesErrorMessage:String = "",

    val isCreateOrderMessageButtonLoading:Boolean = false,
    val isCreateOrderMessageSuccess:Boolean = false,
    val isCreateOrderMessagesError:Boolean = false,
    val createOrderMessageErrorMessage:String = "",


    val isGetPaymentMethodLoading:Boolean= false,
    val isGetPaymentMethodsSuccess:Boolean = false,
    val isGetPaymentMethodsError:Boolean = false,
    val getPyamentMethodsErrorMessage:String = "",

    val isAddPaymentMethodLoading:Boolean= false,
    val isAddPaymentMethodsSuccess:Boolean = false,
    val isAddPaymentMethodsError:Boolean = false,
    val addPyamentMethodsErrorMessage:String = "",

    val isDeletePaymentMethodButtonLoading:Boolean = false,
    val isDeletePaymentMethodSuccess:Boolean=false,
    val isDeletePaymentMethodError:Boolean= false,
    val deletePaymentMethodErrorMessage:String=""

)
