package com.app.leopoly.models

class MultiModel {
    //profile variables
    var userName: String? = null
    var userCode: String? = null
    var userType: String? = null
    var userId: String? = null
    var name: String? = null
    var rootId: String? = null
    var masterId: String? = null
    var enteredQuantity: String? = null
    var countedQty: String? = null
    var mgrName: String? = null
    var mrName: String? = null
    var parentPartyName: String? = null
    var aVGQTY: String? = null
    var isSplit = false
    fun setMasterId(masterId: String?): MultiModel {
        this.masterId = masterId
        return this
    }

    var sCHEDULE2DATE: String? = null
        private set
    var sCHEDULEDATE: String? = null
        private set

    fun setRootId(rootId: String?): MultiModel {
        this.rootId = rootId
        return this
    }

    fun setSCHEDULE2DATE(SCHEDULE2DATE: String?): MultiModel {
        sCHEDULE2DATE = SCHEDULE2DATE
        return this
    }

    fun setSCHEDULEDATE(SCHEDULEDATE: String?): MultiModel {
        sCHEDULEDATE = SCHEDULEDATE
        return this
    }

    var isHighLighted = false

    ///load party
    var partyList: ArrayList<MultiModel>? = null
    var party_id: String? = null
    var party_name: String? = null
    var aCCODE: String? = null
    var aCADD1: String? = null
    var aCADD2: String? = null
    var pINCODE: String? = null
    var aCCITY: String? = null
    var aCADD3: String? = null
    var links: Links? = null
    var pRNTID: String? = null
    var cOMPBIFF: String? = null
    var mOBILE: String? = null
    var eMAIL: String? = null
    var cOMPID: String? = null
    var dLNO2: String? = null
    var dLNO1: String? = null
    var gSTNO: String? = null
    var supplyModel: LastMonthSupplyModel? = null
    var isMR_OR_MANAGER = 0
    var arrayList: ArrayList<MultiModel>? = null

    //     "product_id": "3",
    //            "product_code": "006",
    //            "product_name": "BENZYZINE",
    //            "product_group": "BENZYZINE",
    //            "RES_PER": "",
    //            "category": "",
    //            "CUR_BATHCNO": "",
    //            "CUR_MRP\t": "25.25",
    //            "PKU1": "10",
    //            "PKU2": "10",
    //            "BOXSTRIP": "",
    //            "MARKETEDBY": "LP"
    //product
    var productId: String? = null
    var productCode: String? = null
    var productName: String? = null
    var productGroup: String? = null
    var rES_PER: String? = null
    var category: String? = null
    var cUR_BATCHNO: String? = null
    var cUR_MRP: String? = null
    var pKU1: String? = null
    var pKU2: String? = null
    var bOXSTRIP: String? = null
    var mARLETEDBY: String? = null
    var quantity: String? = null
    var productType: String? = null
    var discount: String? = null
    var qty1: String? = null
        private set
    var qty2: String? = null
        private set
    var date1: String? = null
        private set
    var date2: String? = null
        private set
    var qty1R: String? = null
        private set
    var qty2R: String? = null
        private set

    fun setQty1R(qty1R: String?): MultiModel {
        this.qty1R = qty1R
        return this
    }

    fun setQty2R(qty2R: String?): MultiModel {
        this.qty2R = qty2R
        return this
    }

    fun setQty1(qty1: String?): MultiModel {
        this.qty1 = qty1
        return this
    }

    fun setQty2(qty2: String?): MultiModel {
        this.qty2 = qty2
        return this
    }

    fun setDate1(date1: String?): MultiModel {
        this.date1 = date1
        return this
    }

    fun setDate2(date2: String?): MultiModel {
        this.date2 = date2
        return this
    }

    //dispatch order
    var dISPID: String? = null
    var iNVID: String? = null
    var iNVNO: String? = null
    var iNVDATE: String? = null
    var pRTYID: String? = null
    var aMOUNT: String? = null
    var tRANSPORT: String? = null
    var lRNO: String? = null
    var lRDT: String? = null
    var pARCELS: String? = null
    var wEBLINNK: String? = null

    /*"MRID":"1","MrCode":"M0001
    // ","MrName":"J L RANA",
    // "HQName":"GUJARAT",
    // "MGRID":"1","ValidFrom":"2020-01-01 00:00:00",
    // "ValidTo":"","Email":"haresh_v_panchal@yahoo.com",
    // "Mobile":"9879538164,9924075544","State":"GUJARAT"*/
    var mRID: String? = null
    var mRCODE: String? = null
    var mRNAME: String? = null
    var hQNAME: String? = null
    var mGRID: String? = null
    var vALIDETO: String? = null
    var vaLIDFORM: String? = null
    var sTATE: String? = null

    //pending orders
    var orderList: ArrayList<ProductModel>? = null
    var oRDID: String? = null
    var oRDERNO: String? = null
    var oRDERDATE: String? = null
    var aCID: String? = null
    var dESC1: String? = null
    var managerName: String? = null

    //manager list
    var managerList: ArrayList<MultiModel>? = null
}