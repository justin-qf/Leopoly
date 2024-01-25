package com.app.leopoly.apiHandling.addSalesOrderResponse

import com.google.gson.annotations.SerializedName

class SalesOrderResponse {

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: SalesOrderData? = SalesOrderData()
}