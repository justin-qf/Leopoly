package com.app.leopoly.common

// on date 10-07-2020
object Constant {
    const val user_model = "login_model"
    const val order_model = "order_model"
    const val account_year = "account_year"
    const val packageData = "packageDetail"
    const val TEST_MODE = true
    const val FORM_VALIDATION = false
    const val SPLASH_SCREEN_TIME_OUT = 3000
    const val autoDialogDismissTimeInMlSecLow = 2000
    const val autoDialogDismissTimeInMlSec = 4000
    const val PERMISSION_REQUEST_CODE = 0
    const val NETWORK_ERROR_MESSAGE =
        "LEOPOLY app requires the internet connection. please turn on the data and try again."
    const val SALES_ORDER_DATA = "salesOrderData"

    //constant for dashboard menu
    const val PACKING_SLIP = 1
    const val SALES_ORDERs = 2
    const val DISPATCH = 3
    const val PENDING_ORDER = 4

    //user type
    const val ADMIN = 1
    const val MANAGER = 2
    const val PARTY = 4
    const val MR = 3
    const val UNIT_FLAG_STRIP = "S"
    const val UNIT_FLAG_BOX = "B"

    //API calls
    const val CALL_ORDER_DISPTCH = 1
    const val CALL_PARTY_DETAILS = 2

    const val OBSERVER_SCANNING_DATA = 1
    const val OBSERVER_BACK_FROM_DETAIL_SCREEN = 2
    const val OBSERVER_BACK_FROM_PACKAGE_SLIP = 3

}