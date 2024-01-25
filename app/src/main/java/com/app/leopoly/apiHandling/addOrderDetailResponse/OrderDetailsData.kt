package com.app.leopoly.apiHandling.orderDetailResponse

import com.google.gson.annotations.SerializedName

class OrderDetailsData {
    @SerializedName("RMOUTID"   ) var RMOUTID   : String? = null
    @SerializedName("MEMONO"    ) var MEMONO    : String? = null
    @SerializedName("MEMODATE"  ) var MEMODATE  : String? = null
    @SerializedName("ITEMID"    ) var ITEMID    : String? = null
    @SerializedName("SERIALNO"  ) var SERIALNO  : String? = null
    @SerializedName("DESC1"     ) var DESC1     : String? = null
    @SerializedName("COMPID"    ) var COMPID    : String? = null
    @SerializedName("ACID"      ) var ACID      : String? = null
    @SerializedName("ROLLS"     ) var ROLLS     : String? = null
    @SerializedName("ENTTYPE"   ) var ENTTYPE   : String? = null
    @SerializedName("GROSSWT"   ) var GROSSWT   : String? = null
    @SerializedName("NETWT"     ) var NETWT     : String? = null
    @SerializedName("PRTYID"    ) var PRTYID    : String? = null
    @SerializedName("MASTRID"   ) var MASTRID   : String? = null
    @SerializedName("METER"     ) var METER     : String? = null
    @SerializedName("SIZE"      ) var SIZE      : String? = null
    @SerializedName("THICKNESS" ) var THICKNESS : String? = null
    @SerializedName("COLGRADE"  ) var COLGRADE  : String? = null
    @SerializedName("THICKMOU"  ) var THICKMOU  : String? = null
    @SerializedName("MACHID"    ) var MACHID    : String? = null
    @SerializedName("EMPID"     ) var EMPID     : String? = null
    @SerializedName("LESSWT"    ) var LESSWT    : String? = null
    @SerializedName("BOX"       ) var BOX       : String? = null
    @SerializedName("UNIT"      ) var UNIT      : String? = null
}