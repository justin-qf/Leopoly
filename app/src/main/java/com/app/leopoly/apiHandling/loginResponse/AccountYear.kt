package com.app.leopoly.apiHandling.loginResponse

import com.google.gson.annotations.SerializedName

class AccountYear {
    @SerializedName("ACID")
    var aCID: String? = null

    @SerializedName("ACYEAR")
    var aCYEAR: String? = null

    @SerializedName("ACSDATE")
    var aCSDATE: String? = null

    @SerializedName("ACEDATE")
    var aCEDATE: String? = null

    @SerializedName("COMPID")
    var cOMPID: String? = null

    @SerializedName("ACLOCKDATE")
    var aCLOCKDATE: String? = null

    @SerializedName("TDSLOCKDATE")
    var tDSLOCKDATE: String? = null
}