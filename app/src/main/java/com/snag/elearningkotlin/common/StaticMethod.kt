package com.snag.elearningkotlin.common

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.SeekBar
import android.widget.Spinner
import androidx.core.app.NotificationCompat
import com.snag.elearningkotlin.R
import com.snag.elearningkotlin.model.SheetInfo
import java.util.*

var sheetAPIList: MutableList<SheetInfo> = mutableListOf()
var listLineTemp: MutableList<SheetInfo> = mutableListOf()
var spnOder: Spinner? = null
var sbSpeed: SeekBar? = null
var spnTypeNotify: Spinner? = null


var textToSpeechEng: TextToSpeech? = null
var textToSpeechVie: TextToSpeech? = null

var alarmManager: AlarmManager? = null

var strBtnStop = ""

var prevId = -1
var prevId2 = -1

fun showNotification(
    context: Context,
    title: kotlin.String?,
    body: kotlin.String,
    intent: Intent?,
    notificationManager: NotificationManager
) {

    val stopIntent = Intent()
    stopIntent.action = Constants.ACTION_STOP

    val stopPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, stopIntent, 0)

    val mBuilder = NotificationCompat.Builder(context, "eLearning-01")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .addAction(
            R.drawable.ic_stop, strBtnStop,
            stopPendingIntent
        )
    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addNextIntent(intent)
    val resultPendingIntent = stackBuilder.getPendingIntent(
        0,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    mBuilder.setContentIntent(resultPendingIntent)

    val randomId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
    notificationManager!!.notify(randomId, mBuilder.build())


    if (prevId != -1) {
        if (prevId2 != -1) {
            notificationManager!!.cancel(prevId2)
        }
        prevId2 = prevId
    }
    prevId = randomId

}