package com.app.leopoly.common

import android.util.Log
import com.app.leopoly.models.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object ResponseHandler {
    fun getListFromJSon(food_types: JSONArray?): ArrayList<String>? {
        val strings = ArrayList<String>()
        return if (food_types != null) {
            for (i in 0 until food_types.length()) {
                try {
                    strings.add(food_types.getString(i))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            strings
        } else {
            null
        }
    }

    @JvmStatic
    fun isSuccess(strResponse: String?, jsonResponse: JSONObject?): Boolean {
        if (strResponse != null) {
            val jsonObject = createJsonObject(strResponse)
            if (jsonObject != null) {
                return getBool(jsonObject, "status")
            }
        } else if (jsonResponse != null) {
            return getBool(jsonResponse, "status")
        }
        return false
    }

    @JvmStatic
    fun createJsonObject(response: String?): JSONObject? {
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(response)
            return jsonObject
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getString(jObj: JSONObject, strKey: String?): String {
        return try {
            if (jObj.has(strKey) && !jObj.isNull(strKey)) jObj.getString(strKey) else ""
        } catch (e: JSONException) {
            ""
        }
    }

    fun getInt(jObj: JSONObject, strKey: String?): Int {
        return try {
            if (jObj.has(strKey) && !jObj.isNull(strKey)) jObj.getInt(strKey) else 0
        } catch (e: JSONException) {
            0
        }
    }

    fun getFloat(jObj: JSONObject, strKey: String?): Float {
        return try {
            if (jObj.has(strKey) && !jObj.isNull(strKey)) jObj.getDouble(strKey).toFloat() else 0.0f
        } catch (e: JSONException) {
            0.0f
        }
    }

    @JvmStatic
    fun getBool(jObj: JSONObject, strKey: String?): Boolean {
        return try {
            jObj.has(strKey) && !jObj.isNull(strKey) && jObj.getBoolean(strKey)
        } catch (e: JSONException) {
            false
        }
    }

    private fun getJSONObject(jObj: JSONObject, strKey: String): JSONObject {
        return try {
            if (jObj.has(strKey) && !jObj.isNull(strKey)) jObj.getJSONObject(strKey) else JSONObject()
        } catch (e: JSONException) {
            JSONObject()
        }
    }

    private fun getJSONArray(jObj: JSONObject, strKey: String?): JSONArray {
        return try {
            if (jObj.has(strKey) && !jObj.isNull(strKey)) jObj.getJSONArray(strKey) else JSONArray()
        } catch (e: JSONException) {
            JSONArray()
        }
    }

    @JvmStatic
    fun handleLogin(respo: String?): MultiModel? {
        val response: JSONObject
        var profileModel: MultiModel? = null
        try {
            response = JSONObject(respo)
            if (isSuccess(null, response)) {
                profileModel = MultiModel()
                if (!getJSONArray(response, "data").isNull(0)) {
                    val dataJsonArray = getJSONArray(response, "data")
                    val dataJsonObj = dataJsonArray.getJSONObject(0)
                    profileModel.userCode = getString(dataJsonObj, "USERCODE")
                    profileModel.rootId = getString(dataJsonObj, "master_user")
                    profileModel.userName =
                        Utility.convertFirstUpper(getString(dataJsonObj, "USERNAME"))
                    profileModel.userType = getString(dataJsonObj, "USERTYPE")
                    profileModel.userId = getString(dataJsonObj, "user_id")
                    profileModel.name = getString(dataJsonObj, "name")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return profileModel
    }


    fun handlePaymentDetails(json: String?): ArrayList<PaymentModel?>? {
        val response: JSONObject
        var list: ArrayList<PaymentModel?>? = null
        try {
            response = JSONObject(json)
            if (isSuccess(null, response)) {
/*{
            "id": "1",
            "bill_no": "1",
            "bill_date": "22-07-2020",
            "INVNO": "1",
            "party_name": "ZIRCON SOFTWARE CONSULTANCY",
            "invoce_amount": "1000.00",
            "outstanding_amount": 980,
            "recived amount": "20.00"
        }*/
                list = ArrayList()
                if (!getJSONArray(response, "data").isNull(0)) {
                    val dataJsonArray = getJSONArray(response, "data")
                    for (i in 0 until dataJsonArray.length()) {
                        val `object` = dataJsonArray.getJSONObject(i)
                        val model = PaymentModel()
                        model.id = getString(`object`, "id")
                        model.billNo = getString(`object`, "bill_no")
                        model.billDate = getString(`object`, "bill_date")
                        model.invNo = getString(`object`, "INVNO")
                        model.partyName = getString(`object`, "party_name")
                        model.invoiceAmount = getString(`object`, "invoce_amount")
                        model.outStandingAmount = getString(`object`, "outstanding_amount")
                        model.receivedAmount = getString(`object`, "recived amount")
                        list.add(model)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return list
    }
}