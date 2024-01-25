package com.app.leopoly.apiHandling.orderDetailResponse

import com.google.gson.annotations.SerializedName

class AddOrderDetailResponse {
    @SerializedName("status")
    var status: Int? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("data")
    var data: AddOrderDetailsData? = AddOrderDetailsData()
}