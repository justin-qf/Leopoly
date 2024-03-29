package com.app.leopoly.apiHandling.packgeOrderListResponse

import com.google.gson.annotations.SerializedName


data class PackageOrderListData(

    @SerializedName("INVID") var INVID: String? = null,
    @SerializedName("INVTYPE") var INVTYPE: String? = null,
    @SerializedName("INVNO") var INVNO: String? = null,
    @SerializedName("INVDATE") var INVDATE: String? = null,
    @SerializedName("CHNO") var CHNO: String? = null,
    @SerializedName("CHDATE") var CHDATE: String? = null,
    @SerializedName("ONO") var ONO: String? = null,
    @SerializedName("ODATE") var ODATE: String? = null,
    @SerializedName("PRTYID") var PRTYID: String? = null,
    @SerializedName("TRPID") var TRPID: String? = null,
    @SerializedName("LRNO") var LRNO: String? = null,
    @SerializedName("LRDATE") var LRDATE: String? = null,
    @SerializedName("BOX") var BOX: String? = null,
    @SerializedName("CFORMNO") var CFORMNO: String? = null,
    @SerializedName("FFORMNO") var FFORMNO: String? = null,
    @SerializedName("DAYS") var DAYS: String? = null,
    @SerializedName("GAMOUNT") var GAMOUNT: String? = null,
    @SerializedName("DISCPER") var DISCPER: String? = null,
    @SerializedName("DISCOUNT") var DISCOUNT: String? = null,
    @SerializedName("EXPER") var EXPER: String? = null,
    @SerializedName("EXCISE") var EXCISE: String? = null,
    @SerializedName("CESSPER") var CESSPER: String? = null,
    @SerializedName("CESSAMT") var CESSAMT: String? = null,
    @SerializedName("STPER") var STPER: String? = null,
    @SerializedName("ST") var ST: String? = null,
    @SerializedName("SURPER") var SURPER: String? = null,
    @SerializedName("SURCHARGE") var SURCHARGE: String? = null,
    @SerializedName("CST") var CST: String? = null,
    @SerializedName("CSTAMT") var CSTAMT: String? = null,
    @SerializedName("FREIGHT") var FREIGHT: String? = null,
    @SerializedName("PLUS") var PLUS: String? = null,
    @SerializedName("PLUSDESC") var PLUSDESC: String? = null,
    @SerializedName("OTHPER") var OTHPER: String? = null,
    @SerializedName("OTHER") var OTHER: String? = null,
    @SerializedName("AMOUNT") var AMOUNT: String? = null,
    @SerializedName("RECAMT") var RECAMT: String? = null,
    @SerializedName("BILLDISC") var BILLDISC: String? = null,
    @SerializedName("COMPID") var COMPID: String? = null,
    @SerializedName("ACID") var ACID: String? = null,
    @SerializedName("STID") var STID: String? = null,
    @SerializedName("TMPDATE") var TMPDATE: String? = null,
    @SerializedName("REMDATE") var REMDATE: String? = null,
    @SerializedName("REMTIME") var REMTIME: String? = null,
    @SerializedName("RTINWORD") var RTINWORD: String? = null,
    @SerializedName("PRETIME") var PRETIME: String? = null,
    @SerializedName("SHECESSPER") var SHECESSPER: String? = null,
    @SerializedName("SHECESS") var SHECESS: String? = null,
    @SerializedName("DESC1") var DESC1: String? = null,
    @SerializedName("EXINV") var EXINV: String? = null,
    @SerializedName("BUYERID") var BUYERID: String? = null,
    @SerializedName("EXINVTYPE") var EXINVTYPE: String? = null,
    @SerializedName("EXINVDESC") var EXINVDESC: String? = null,
    @SerializedName("LOADPER") var LOADPER: String? = null,
    @SerializedName("LOADCHARGE") var LOADCHARGE: String? = null,
    @SerializedName("BROKER") var BROKER: String? = null,
    @SerializedName("VEHINO") var VEHINO: String? = null,
    @SerializedName("AGID") var AGID: String? = null,
    @SerializedName("DESTI") var DESTI: String? = null,
    @SerializedName("COMMNAME") var COMMNAME: String? = null,
    @SerializedName("TARIFFNO") var TARIFFNO: String? = null,
    @SerializedName("ITEMID") var ITEMID: String? = null,
    @SerializedName("LESSWT") var LESSWT: String? = null,
    @SerializedName("SORDNO") var SORDNO: String? = null,
    @SerializedName("SORDITID") var SORDITID: String? = null,
    @SerializedName("PCODE") var PCODE: String? = null,
    @SerializedName("PNAME") var PNAME: String? = null,
    @SerializedName("ICODE") var ICODE: String? = null,
    @SerializedName("ITEMNAME") var ITEMNAME: String? = null,
    @SerializedName("BALQTY") var BALQTY: String? = null

)