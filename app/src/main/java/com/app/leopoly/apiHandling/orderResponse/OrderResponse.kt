package com.app.leopoly.apiHandling.orderResponse

import com.google.gson.annotations.SerializedName

class OrderResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: OrderData? = null
}