package com.app.leopoly.models

class ProductModel {
    var dISPTID: String? = null
    var dISPID: String? = null
    var oRDITID: String? = null
    var oRDID: String? = null
    var pRODID: String? = null
    var sTRIPQTY: String? = null
    var bOXQTY: String? = null
    var pRODUCTNAME: String? = null
    var enteredQTY: String? = null
    var countedQTY: String? = null
    var bOXSTRIPMAX: String? = null
    var productType: String? = null
    var editQTy: String? = null
    var sTRIPQTY2: String? = null
        private set
    var bOXQTY2: String? = null
        private set
    var aVGQTY: String? = null
        private set
    var qty1R: String? = null
        private set
    var qty2R: String? = null
        private set

    fun setQty1R(qty1R: String?): ProductModel {
        this.qty1R = qty1R
        return this
    }

    fun setQty2R(qty2R: String?): ProductModel {
        this.qty2R = qty2R
        return this
    }

    fun setAVGQTY(AVGQTY: String?): ProductModel {
        aVGQTY = AVGQTY
        return this
    }

    var qty1: String? = null
        private set
    var qty2: String? = null
        private set
    var date1: String? = null
        private set
    var date2: String? = null
        private set

    fun setQty1(qty1: String?): ProductModel {
        this.qty1 = qty1
        return this
    }

    fun setQty2(qty2: String?): ProductModel {
        this.qty2 = qty2
        return this
    }

    fun setDate1(date1: String?): ProductModel {
        this.date1 = date1
        return this
    }

    fun setDate2(date2: String?): ProductModel {
        this.date2 = date2
        return this
    }

    fun setSTRIPQTY2(STRIPQTY2: String?): ProductModel {
        sTRIPQTY2 = STRIPQTY2
        return this
    }

    fun setBOXQTY2(BOXQTY2: String?): ProductModel {
        bOXQTY2 = BOXQTY2
        return this
    }
}