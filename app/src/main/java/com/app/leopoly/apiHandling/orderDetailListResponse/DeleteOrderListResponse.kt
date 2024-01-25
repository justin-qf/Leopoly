package com.app.leopoly.apiHandling.orderDetailListResponse

import com.google.gson.annotations.SerializedName

class DeleteOrderListResponse {
    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

}