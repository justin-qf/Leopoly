package com.app.leopoly.apiHandling.addSalesOrderResponse

import com.google.gson.annotations.SerializedName

class SalesOrderData {

    @SerializedName("INVID")
    var INVID: String? = null

    @SerializedName("INVDATE")
    var INVDATE: String? = null

    @SerializedName("INVNO")
    var INVNO: String? = null

    @SerializedName("ONO")
    var ONO: String? = null

    @SerializedName("ODATE")
    var ODATE: String? = null

    @SerializedName("PRTYID")
    var PRTYID: String? = null

    @SerializedName("TRPID")
    var TRPID: String? = null

    @SerializedName("DAYS")
    var DAYS: String? = null

    @SerializedName("DESTI")
    var DESTI: String? = null

    @SerializedName("COMPID")
    var COMPID: String? = null

    @SerializedName("ACID")
    var ACID: String? = null

    @SerializedName("sales_order_details")
    var salesOrderDetails: ArrayList<SalesOrderDetails> = arrayListOf()
}

data class SalesOrderDetails(

    @SerializedName("INVITID") var INVITID: String? = null,
    @SerializedName("INVID") var INVID: String? = null,
    @SerializedName("ITEMID") var ITEMID: String? = null,
    @SerializedName("PACK") var PACK: String? = null,
    @SerializedName("LESSWT") var LESSWT: String? = null,
    @SerializedName("QTY") var QTY: String? = null,
    @SerializedName("RATE") var RATE: String? = null,
    @SerializedName("AMOUNT") var AMOUNT: String? = null,
    @SerializedName("ITMDESC") var ITMDESC: String? = null,
    @SerializedName("COMPID") var COMPID: String? = null,
    @SerializedName("ACID") var ACID: String? = null

)