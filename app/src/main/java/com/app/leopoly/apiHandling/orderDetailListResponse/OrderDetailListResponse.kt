package com.app.leopoly.apiHandling.orderDetailListResponse

import com.google.gson.annotations.SerializedName

class OrderDetailListResponse {

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<OrderDetailListData> = arrayListOf()
}