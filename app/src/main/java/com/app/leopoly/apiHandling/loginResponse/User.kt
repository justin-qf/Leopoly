package com.app.leopoly.apiHandling.loginResponse

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("USERID")
    var uSERID: String? = null

    @SerializedName("USERNAME")
    var uSERNAME: String? = null

    @SerializedName("USERCODE")
    var uSERCODE: String? = null

    @SerializedName("PASSWORD")
    var pASSWORD: String? = null

    @SerializedName("PASSDATE")
    var pASSDATE: String? = null

    @SerializedName("PACKSLIP")
    var PACKSLIP: String? = null

    @SerializedName("ORDERREG")
    var ORDERREG: String? = null

    @SerializedName("PSUPDATE")
    var PSUPDATE: String? = null

    @SerializedName("DISPACTH")
    var DISPACTH: String? = null

    @SerializedName("PENDING")
    var pENDING: String? = null

    @SerializedName("account_year")
    var accountYear: List<AccountYear>? = null
}