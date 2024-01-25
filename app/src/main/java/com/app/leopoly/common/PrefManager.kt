package com.app.leopoly.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.app.leopoly.apiHandling.loginResponse.AccountYear
import com.app.leopoly.apiHandling.loginResponse.User
import com.app.leopoly.apiHandling.orderDetailResponse.OrderDetailsData
import com.app.leopoly.apiHandling.orderResponse.OrderData
import com.app.leopoly.models.MultiModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefManager @SuppressLint("CommitPrefEdits") constructor(context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val onOrderChange = "orderChangeData"

    init {
        // shared pref mode
        val PRIVATE_MODE = 0
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    var loginData: User?
        get() {
            val gson = Gson()
            val json = pref.getString(Constant.user_model, "")
            return if (json == null) User() else gson.fromJson(
                json,
                User::class.java
            )
        }
        set(obj) {
            val gson = Gson()
            val json = gson.toJson(obj)
            editor.putString(Constant.user_model, json)
            editor.apply()
        }

    var orderData: OrderData?
        get() {
            val gson = Gson()
            val json = pref.getString(Constant.order_model, "")
            return if (json == null) OrderData() else gson.fromJson(
                json,
                OrderData::class.java
            )
        }
        set(obj) {
            val gson = Gson()
            val json = gson.toJson(obj)
            editor.putString(Constant.order_model, json)
            editor.apply()
        }

    var packingDetail: OrderDetailsData?
        get() {
            val gson = Gson()
            val json = pref.getString(Constant.packageData, "")
            return if (json == null) OrderDetailsData() else gson.fromJson(
                json,
                OrderDetailsData::class.java
            )
        }
        set(obj) {
            val gson = Gson()
            val json = gson.toJson(obj)
            editor.putString(Constant.packageData, json)
            editor.apply()
        }

    var accountYear: List<AccountYear?>?
        get() {
            val gson = Gson()
            val json = pref.getString(Constant.account_year, "")
            return if (json!!.isEmpty()) ArrayList() // Return an empty list if no data is found
            else {
                val type = object : TypeToken<List<AccountYear?>?>() {}.type
                gson.fromJson<List<AccountYear>>(json, type)
            }
        }
        set(accountYearList) {
            val gson = Gson()
            val json = gson.toJson(accountYearList)
            editor.putString(Constant.account_year, json)
            editor.apply()
        }
    var totalBoxes: String?
        get() = pref.getString(BOXES, "")
        set(Boxes) {
            editor.putString(BOXES, Boxes)
            editor.commit()
            editor.apply()
        }
    var totalGross: String?
        get() = pref.getString(GROSS, "")
        set(Gross) {
            editor.putString(GROSS, Gross)
            editor.commit()
            editor.apply()
        }

    var totalAmount: String?
        get() = pref.getString(AMOUNT, "0.0")
        set(Gross) {
            editor.putString(AMOUNT, Gross)
            editor.commit()
            editor.apply()
        }
    var totalNetWT: String?
        get() = pref.getString(TOTAL_NET, "")
        set(Total_net) {
            editor.putString(TOTAL_NET, Total_net)
            editor.commit()
            editor.apply()
        }

    var ipAddress: String?
        get() = pref.getString(IP_ADDRESS, "")
        set(day) {
            editor.putString(IP_ADDRESS, day)
            editor.commit()
            editor.apply()
        }

    var scannerId: String?
        get() = pref.getString(SCANNER_ID, "")
        set(scannerId) {
            editor.putString(SCANNER_ID, scannerId)
            editor.commit()
            editor.apply()
        }
    var runOnce: Int
        get() = pref.getInt(RUN_ONCE, 0)
        set(day) {
            editor.putInt(RUN_ONCE, day)
            editor.commit()
            editor.apply()
        }
    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }
    var isLogin: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)
        set(token) {
            editor.putBoolean(IS_LOGIN, token)
            editor.commit()
            editor.apply()
        }
    var userProfile: MultiModel?
        get() {
            val gson = Gson()
            return gson.fromJson(pref.getString(USER_PROFILE, ""), MultiModel::class.java)
        }
        set(profile) {
            val gson = Gson()
            editor.putString(USER_PROFILE, gson.toJson(profile))
            editor.commit()
            editor.apply()
        }

    fun Logout() {
        editor.clear()
        editor.commit()
        editor.apply()
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false)
        editor.commit()
        editor.apply()
    }

    var editedObject: String?
        get() = pref.getString("editedOrders", "")
        set(json) {
            editor.putString("editedOrders", json)
            editor.commit()
            editor.apply()
        }

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "leopoly"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val USER_PROFILE = "user_profile_details"
        private const val IP_ADDRESS = "ipAddress"
        private const val BOXES = "boxes"
        private const val GROSS = "gross"
        private const val AMOUNT = "amount"
        private const val TOTAL_NET = "total_net"
        private const val RUN_ONCE = "runOnceA_Day"
        private const val IS_LOGIN = "isLogin"
        private const val SCANNER_ID = "scannerId"
    }
}