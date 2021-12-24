package com.snag.elearningkotlin

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.snag.elearningkotlin.common.*
import com.snag.elearningkotlin.model.SheetInfo
import com.snag.elearningkotlin.receiver.NotificationPublisher
import com.snag.elearningkotlin.receiver.NotificationReceiverActivity
import com.snag.elearningkotlin.service.GoogleService
import java.lang.String
import java.util.*
import kotlin.String as String1

class MainActivity : AppCompatActivity() {

    private var btnStart: Button? = null
    private var btnStop: Button? = null
    private var txtStatus: TextView? = null
    private var txtField: EditText? = null

    private var txtTime: EditText? = null

    private var spnSheet: Spinner? = null
    private var spnVoice: Spinner? = null

    private var textToSpeech: TextToSpeech? = null

    //
    private val googleService = GoogleService()

    private var resultIntent: Intent? = null

    private var notificationManager: NotificationManager? = null
//    private var wakeLock: PowerManager.WakeLock? = null

    private var prevId =-1
    private var prevId2 =-1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById<View>(R.id.btnStart) as Button
        btnStop = findViewById<View>(R.id.btnStop) as Button
        txtField = findViewById<View>(R.id.txtField) as EditText
        txtStatus = findViewById<View>(R.id.txtStatus) as TextView
        txtTime = findViewById<View>(R.id.txtTime) as EditText
        sbSpeed = findViewById<SeekBar>(R.id.sbSpeed)

        sbSpeed!!.max = 200;
        sbSpeed!!.progress = 70;

        resultIntent = Intent(this@MainActivity, NotificationReceiverActivity::class.java)
        notificationManager =
            this@MainActivity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        strBtnStop = getString(R.string.btn_stop)

        spnSheet = findViewById(R.id.spnSheet)
        ArrayAdapter.createFromResource(
            this, R.array.type_noti_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnSheet!!.adapter = adapter
        }

        spnOder = findViewById(R.id.spnOder)
        ArrayAdapter.createFromResource(
            this, R.array.type_order, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnOder!!.adapter = adapter
        }

        spnTypeNotify = findViewById(R.id.spnTypeNotify)
        ArrayAdapter.createFromResource(
            this, R.array.type_notify, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnTypeNotify!!.adapter = adapter
        }

        spnVoice = findViewById(R.id.spnVoice)
        ArrayAdapter.createFromResource(
            this, R.array.spnVoice, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnVoice!!.adapter = adapter
        }

        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Constants.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
        )
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
        )
        checkPermission(
            Manifest.permission.WAKE_LOCK,
            Constants.REQUEST_PERMISSION_INTERNET
        )

        checkPermission(
            Manifest.permission.INTERNET,
            Constants.REQUEST_PERMISSION_WAKE_LOCK
        )
        textToSpeechEng = TextToSpeech(
            this@MainActivity
        ) { status -> // TODO Auto-generated method stub
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(
                    this@MainActivity,
                    "Initilization textToSpeechEng Failed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        textToSpeechVie = TextToSpeech(
            this@MainActivity
        ) { status -> // TODO Auto-generated method stub
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(
                    this@MainActivity,
                    "Initilization textToSpeechVie Failed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        textToSpeech = TextToSpeech(
            this@MainActivity
        ) { status -> // TODO Auto-generated method stub
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(
                    this@MainActivity,
                    "Initilization textToSpeechVie Failed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        btnStart!!.setOnClickListener {
            checkSupportLang(Locale("vi"))
            checkSupportLang(Locale.US)
            Toast.makeText(
                this@MainActivity,
                "" + Locale.getDefault() + " is default lang",
                Toast.LENGTH_SHORT
            )
                .show()

            textToSpeechVie!!.language = Locale("vi")
            onNotify(sheetAPIList)
        }
        btnStop!!.setOnClickListener {
            onStopNotify()
        }

        spnSheet?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //this func has nothing to do
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setDataTextBox(sheetAPIList)
                listLineTemp = sheetAPIList.toMutableList()
            }
        }

        spnTypeNotify?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //this func has nothing to do
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                showNotifyForStop(this@MainActivity)
            }
        }

        spnVoice?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //this func has nothing to do
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (spnVoice!!.selectedItem.toString()) {
                    "US" -> {
                        textToSpeechEng!!.language = Locale.US
                    }
                    "UK" -> {
                        textToSpeechEng!!.language = Locale.UK
                    }
                    "ENGLISH" -> {
                        textToSpeechEng!!.language = Locale.ENGLISH
                    }
                    "en-NZ" -> {
                        textToSpeechEng!!.language = Locale("en-NZ")
                    }
                    "en-US" -> {
                        textToSpeechEng!!.language = Locale("en-NZ")
                    }
                    "en-GB" -> {
                        textToSpeechEng!!.language = Locale("en-NZ")
                    }
                    "en-AU" -> {
                        textToSpeechEng!!.language = Locale("en-NZ")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showNotifyForStop(context: Context) {
        val typeNotify = spnTypeNotify!!.selectedItem.toString()
        if (!isStop && (typeNotify == "-vie" || typeNotify == "-voice" || typeNotify == "-eng")) {
            showNotification( context, "ELearningKotlin", Constants.EMPTY_VALUE,  resultIntent , notificationManager!!)
        }
    }
    private fun onStopNotify() {
        txtStatus!!.text = Constants.STT_OFF
        isStop = true
        notificationManager!!.cancelAll()
        if(alarmManager!= null){
            val pendingIntent = PendingIntent.getBroadcast(
                this@MainActivity, 1, intent, 0
            )
            alarmManager!!.cancel(pendingIntent)
        }
//        if (wakeLock != null && wakeLock!!.isHeld) {
//            wakeLock!!.release()
//            Toast.makeText(this@MainActivity, "wakeLock is off ", Toast.LENGTH_SHORT)
//                .show()
//        }
    }

    private fun onNotify(sheetAPIList: MutableList<SheetInfo>) {

        if (sheetAPIList.isNotEmpty()) {
            isStop = false
            showNotifyForStop(this@MainActivity)
            txtStatus!!.text = Constants.STT_ON
           val time: Long = txtTime!!.text.toString().toLong()

            val notificationIntent = Intent(this, NotificationPublisher::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating (AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, time, pendingIntent)
        }
    }


    private fun setDataTextBox(intList: MutableList<SheetInfo>) {
        intList.clear()
        val thread = Thread {
            try {
                val sheetItems = googleService.getData(
                    this@MainActivity,
                    spnSheet!!.selectedItem.toString()
                )
                if (sheetItems != null) {
                    for (item in sheetItems) {
                        val arrItem: List<String> = item as List<String>

                        if (arrItem.isNotEmpty() && arrItem.elementAt(0).isNotBlank()) {
                            val eng = arrItem.elementAt(0)
                            var vie = ""
                            if (arrItem.size > 1) {
                                vie = arrItem.elementAt(1).toString()

                            }

                            if (arrItem.size > 2 && arrItem.elementAt(2).isNotEmpty()) {
                                vie = arrItem.elementAt(2).toString()
                            }

                            val sheetInfo = SheetInfo(eng, vie)

                            intList.add(sheetInfo)
                        }
                    }

                    var strTextBox = ""
                    for (sheetInfo in intList) {
                        strTextBox += "" + sheetInfo.eng + " " + Constants.SPLIT + " " + sheetInfo.vie + "\n"
                    }
                    runOnUiThread {
                        txtField!!.setText(strTextBox)
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun checkSupportLang(locale: Locale) {
        var result = textToSpeech!!.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Toast.makeText(this@MainActivity, "$locale is not supported", Toast.LENGTH_SHORT)
                .show()
        }
    }



    ///
    private fun checkPermission(permission: String1, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(
                this@MainActivity,
                "Permission $permission already granted",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String1>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                checkGrantPermission(grantResults, "WRITE_EXTERNAL_STORAGE")
            }
            Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE -> {
                checkGrantPermission(grantResults, "READ_EXTERNAL_STORAGE")
            }
            Constants.REQUEST_PERMISSION_INTERNET -> {
                checkGrantPermission(grantResults, "INTERNET")
            }
            Constants.REQUEST_PERMISSION_WAKE_LOCK -> {
                checkGrantPermission(grantResults, "WAKE_LOCK")
            }
        }

    }

    private fun checkGrantPermission(grantResults: IntArray, strPermission: String1) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                this@MainActivity,
                "$strPermission Permission Granted",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "$strPermission Permission Denied",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}