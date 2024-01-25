package com.app.leopoly.apiHandling.orderListResponse

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class OrderListResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<OrderListData>? = null
}