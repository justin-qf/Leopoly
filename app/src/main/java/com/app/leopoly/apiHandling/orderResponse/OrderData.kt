package com.app.leopoly.apiHandling.orderResponse

import com.google.gson.annotations.SerializedName

class OrderData {
    @SerializedName("INVID")
    var iNVID: String? = null

    @SerializedName("INVNO")
    var iNVNO: String? = null

    @SerializedName("INVDATE")
    var iNVDATE: String? = null

    @SerializedName("PRTYID")
    var pRTYID: String? = null

    @SerializedName("COMPID")
    var cOMPID: String? = null

    @SerializedName("ACID")
    var aCID: String? = null

    @SerializedName("ITEMID")
    var iTEMID: String? = null

    @SerializedName("SORDNO")
    var sORDNO: String? = null

    @SerializedName("SORDITID")
    var sORDITID: String? = null
}