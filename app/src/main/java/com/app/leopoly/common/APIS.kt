package com.app.leopoly.common

object APIS {
    //root url
    const val ipConfig = "192.168.42.71"

    //public static final String URL="http://"+ipConfig+"/lagroup/";
    const val URL = ""

    //Login
    const val LOGIN = URL + "login"
    const val GET_ALL_PRODUCT = URL + "getAllProduct"
    const val GET_PRODUCT_ID = URL + "getProductById"
    const val GET_ALL_PARTY = URL + "getAllParty" // getAllParty / 1
    const val GET_PARTY_BY_ID = URL + "getPartyById"
    const val ADD_ORDER = URL + "addOrder"
    const val FORGOT_PASSWORD = URL + "forgetPassword"
    const val VERIFY_OTP = URL + "verifyOtp"
    const val NEW_PASSWORD = URL + "addPassword"
    const val GET_DISPETCH_ORDER = URL + "getDispatchOrder/1"
    const val GET_MR_BYE_MANAGER = URL + "getMrByManager"
    const val GET_PENDING_ORDER = URL + "getPendingOrder"

    //  order_number:""  party_id:""   mr_id:""   manager_id:""    ACID:""discount_1:""   product:[]   //
    const val ADD_PARTY_REQUEST = URL + "addPartyRequest"
    const val GET_PAYMETN_DETAILS = URL + "getPaymentDetails"
    const val ALL_MANAGER = URL + "getAllManager"
    const val EDIT_PENDING_ORDERS = URL + "editPendingOrder"
    const val VERIFY_EDIT_ORDER = URL + "verifyEditOrder"
    const val GET_SUB_PARTY = URL + "getSubPartyByPartyId"
    const val ADD_REMARKS = URL + "addRemarks"
    const val GET_OTP = URL + "getotp"
    const val CHECK_MR = URL + "getManager"
    const val CHECK_USER = URL + "checkPhone"
}