package com.app.leopoly.apiHandling.orderDetailResponse

import com.google.gson.annotations.SerializedName

class AddOrderDetailsData {
    @SerializedName("INVITID" ) var INVITID : String? = null
    @SerializedName("SIZE"    ) var SIZE    : String? = null
    @SerializedName("ROLLNO"  ) var ROLLNO  : String? = null
    @SerializedName("BOXES"   ) var BOXES   : String? = null
    @SerializedName("GROSSWT" ) var GROSSWT : String? = null
    @SerializedName("NETWT"   ) var NETWT   : String? = null
    @SerializedName("UNIT"    ) var UNIT    : String? = null
    @SerializedName("ITMDESC" ) var ITMDESC : String? = null
    @SerializedName("INVID"   ) var INVID   : String? = null
    @SerializedName("RMOUTID" ) var RMOUTID : String? = null
}