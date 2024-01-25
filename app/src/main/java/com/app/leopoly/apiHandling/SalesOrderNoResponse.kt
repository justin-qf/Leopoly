package com.app.leopoly.apiHandling

import com.google.gson.annotations.SerializedName

class SalesOrderNoResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("data")
    var data: String? = null
}