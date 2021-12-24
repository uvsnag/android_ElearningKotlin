package com.snag.elearningkotlin.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.snag.elearningkotlin.common.Constants
import com.snag.elearningkotlin.common.isStop
import com.snag.elearningkotlin.common.txtStatus


class NotificationReceiver : BroadcastReceiver () {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        if (Constants.ACTION_STOP == action) {
            isStop = true
            txtStatus!!.text=Constants.STT_OFF
            if (context != null) {
                val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }

    }
}