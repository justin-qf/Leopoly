package com.app.leopoly.apiHandling.partyResponse

import com.google.gson.annotations.SerializedName

class TransportResponse {

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<TransportListData> = arrayListOf()
}