package com.app.leopoly.apiHandling.orderDetailResponse
import com.google.gson.annotations.SerializedName

class OrderDetailResponse {
    @SerializedName("status"  ) var status  : Int?    = null
    @SerializedName("message" ) var message : String? = null
    @SerializedName("data"    ) var data    : OrderDetailsData?   = OrderDetailsData()
}