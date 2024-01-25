package com.app.leopoly.apiHandling.packageSlipResponse

import com.google.gson.annotations.SerializedName

class PackageSlipResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<PackageSlipData>? = null

    @SerializedName("packing_slip")
    var packingSlip: String? = null

    @SerializedName("item_list")
    var itemList: List<PackageSlipList>? = null
}