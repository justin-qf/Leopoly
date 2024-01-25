package com.app.leopoly.apiHandling.itemResponse

import com.google.gson.annotations.SerializedName

class ItemResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<ItemListData>? = null
}