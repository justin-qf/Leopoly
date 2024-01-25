package com.app.leopoly.apiHandling.lessWtResponse

import com.google.gson.annotations.SerializedName

class LessWtResponse {

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("data")
    var data: ArrayList<LessWtData> = arrayListOf()
}