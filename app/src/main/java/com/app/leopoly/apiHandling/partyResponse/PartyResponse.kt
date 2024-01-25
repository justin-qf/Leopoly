package com.app.leopoly.apiHandling.partyResponse

import com.google.gson.annotations.SerializedName

class PartyResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<PartyListData>? = null
}