package com.app.leopoly.common

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private var TYPE_NOT_CONNECTED = -1
    fun getConnectivityStatus(context: Context): Boolean {
        val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetwork = cm.activeNetworkInfo
        return if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) true else activeNetwork.type == ConnectivityManager.TYPE_MOBILE
        } else false
    }

    fun getStrConnectivityStatus(context: Context): Int {
        val cm = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            TYPE_NOT_CONNECTED = -1
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        } else {
            TYPE_NOT_CONNECTED = 0
        }
        return TYPE_NOT_CONNECTED
    }
}