package com.app.leopoly.common

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import com.app.leopoly.BaseActivity

class NetworkChangeReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (isOnline(context)) {
                BaseActivity.InternetError(true)
            } else {
                BaseActivity.InternetError(false)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    companion object {
        private fun isOnline(context: Context): Boolean {
            return try {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                //should check null because in airplane mode it will be null
                netInfo != null && netInfo.isConnected
            } catch (e: NullPointerException) {
                e.printStackTrace()
                false
            }
        }
    }
}