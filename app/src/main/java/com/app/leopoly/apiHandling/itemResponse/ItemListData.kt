package com.app.leopoly.apiHandling.itemResponse

import com.google.gson.annotations.SerializedName

class ItemListData {

    @SerializedName("ITEMID")
    var itemid: String? = null

    @SerializedName("ITEMNAME")
    val itemname: String? = null

    @SerializedName("GRPID")
    val grpid: String? = null

    @SerializedName("SGRPID")
    val sgrpid: String? = null

    @SerializedName("HSNCODE")
    val hsncode: String? = null

    @SerializedName("VATPER")
    val vatper: String? = null

    @SerializedName("SALESRATE")
    val salesrate: String? = null

    @SerializedName("PURCRATE")
    val purcrate: String? = null

    @SerializedName("UNIT")
    val unit: String? = null

    @SerializedName("DECIMAL")
    val decimal: String? = null

    @SerializedName("COMPANY")
    val company: String? = null

    @SerializedName("EXCISRATE")
    val excisrate: String? = null

    @SerializedName("MINIMUM")
    val minimum: String? = null

    @SerializedName("MAXIMUM")
    val maximum: String? = null

    @SerializedName("ORDQTY")
    val ordqty: String? = null

    @SerializedName("OPSTOCK")
    val opstock: String? = null

    @SerializedName("ITEMTYPE")
    val itemtype: String? = null

    @SerializedName("OPAMOUNT")
    val opamount: String? = null

    @SerializedName("ICODE")
    val icode: String? = null

    @SerializedName("CETID")
    val cetid: String? = null

    @SerializedName("SIZE")
    val size: String? = null

    @SerializedName("THICKNESS")
    val thickness: String? = null

    @SerializedName("METER")
    val meter: String? = null

    @SerializedName("ROLLS")
    val rolls: String? = null
}