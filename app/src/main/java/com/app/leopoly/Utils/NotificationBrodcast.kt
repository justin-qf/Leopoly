package com.app.ssn.Utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBrodcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.extras!!.getInt("notificationId")
        /* Your code to handle the event here */if (notificationId == 0) {
            val notify_manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancel(0)
            notify_manager.cancelAll()
        }
    }
}