package com.app.leopoly.apiHandling.packgeOrderListResponse

import com.google.gson.annotations.SerializedName

data class PackageOrderListResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<PackageOrderListData> = arrayListOf()
)