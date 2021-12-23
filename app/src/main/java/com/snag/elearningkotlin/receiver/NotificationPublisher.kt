package com.snag.elearningkotlin.receiver;

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.snag.elearningkotlin.*
import com.snag.elearningkotlin.common.*
import com.snag.elearningkotlin.model.SheetInfo
import java.util.*

class NotificationPublisher : BroadcastReceiver() {

    private var notificationManager: NotificationManager? = null
    private var resultIntent: Intent? = null
    private val order = spnOder!!.selectedItem.toString()


    override fun onReceive(context: Context, intent: Intent) {
        resultIntent = Intent(context, NotificationReceiverActivity::class.java)
        notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        if (isStop) {
            if(alarmManager!= null){
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 1, intent, 0
                )
                alarmManager!!.cancel(pendingIntent)
            }
            return
        }

        if (listLineTemp.isEmpty()) {
            listLineTemp = sheetAPIList.toMutableList()
        }
        var i = 0
        if (order == "Sequence") {

        }
        if (order == "Random") {
            i = (0 until listLineTemp.size).random()
        }
        val line = listLineTemp[i]
        onProcessNotifyVoice(line, context)
        listLineTemp.removeAt(i)
    }

    private fun onProcessNotifyVoice(line: SheetInfo, context:Context) {


        val strEng: String = line.eng.toString()
        val strVie: String = line.vie

        when (spnTypeNotify!!.selectedItem.toString()) {
            "-eng" -> {
                onSpeak(strEng, strVie, Constants.LANG_ENG)
            }
            "-voice" -> {
                onSpeak(strEng, strVie, Constants.LANG_ENG_VIE)
            }
            "eng-" -> {
                showNotification(context, strEng, Constants.EMPTY_VALUE, resultIntent, notificationManager!!)
            }
            "noti-" -> {
                showNotification(context, strEng, strVie, resultIntent, notificationManager!!)
            }
            "eng-eng" -> {
                showNotification(context, strEng, Constants.EMPTY_VALUE, resultIntent,notificationManager!!)
                onSpeak(strEng, strVie, Constants.LANG_ENG)
            }
            "noti-voice" -> {
                showNotification(context, strEng, strVie, resultIntent, notificationManager!!)
                onSpeak(strEng, strVie, Constants.LANG_ENG_VIE)
            }
            "noti-eng" -> {
                showNotification(context, strEng, strVie, resultIntent, notificationManager!!)
                onSpeak(strEng, strVie, Constants.LANG_ENG)
            }
            "-vie" -> {
                onSpeak(strEng, strVie, Constants.LANG_VIE)
            }
            else -> {
                //
            }
        }

    }

    private fun onSpeak(speakEng: kotlin.String, strVie: kotlin.String, strLang: kotlin.String) {

        val speedVoid: Float = (sbSpeed!!.progress.toFloat() / 100)
        textToSpeechEng!!.setSpeechRate(speedVoid)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when (strLang) {
                Constants.LANG_ENG -> {
                    textToSpeechEng!!.speak(speakEng, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                Constants.LANG_VIE -> {
                    textToSpeechVie!!.speak(strVie, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                Constants.LANG_ENG_VIE -> {
                    textToSpeechEng!!.speak(speakEng, TextToSpeech.QUEUE_FLUSH, null, null)
//                    TimeUnit.SECONDS.sleep(2L)

                    textToSpeechVie!!.speak(strVie, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                else -> {
                    //mo
                }
            }
        } else {
            when (strLang) {
                Constants.LANG_ENG -> {
                    textToSpeechEng!!.speak(speakEng, TextToSpeech.QUEUE_FLUSH, null)
                }
                Constants.LANG_VIE -> {
                    textToSpeechVie!!.speak(strVie, TextToSpeech.QUEUE_FLUSH, null)
                }
                Constants.LANG_ENG_VIE -> {
                    textToSpeechEng!!.speak(speakEng, TextToSpeech.QUEUE_FLUSH, null)
                    textToSpeechVie!!.speak(strVie, TextToSpeech.QUEUE_FLUSH, null)
                }
                else -> {
                    //mo
                }
            }
        }
    }


}