package com.snag.elearningkotlin.service

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.snag.elearningkotlin.R
import java.io.InputStream
import java.util.*

class GoogleService {
    private val spreadsheetId =
        "1w_LXJm2Vsw11IlhyuwaMJEWQLT36kgrJ3fJUQwA37fg" // id của file google của bạn

    //    private val range = "'Trang tính2'!B2:G5" // Phạm vi bạn muốn đọc dữ liệu ở sheet google
//    private var range = "'Words1'!A1:C100" // Phạm vi bạn muốn đọc dữ liệu ở sheet google
    private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)
    public fun getData(context: Context?, strSheet :String): List<Any>? {

        var range = "'$strSheet'!A1:C100"
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val resourceAsStream: InputStream = context?.getResources()?.openRawResource(R.raw.cre)
            ?: throw Exception()

        val credential = GoogleCredential.fromStream(resourceAsStream).createScoped(scopes)

        return try {
            credential.refreshToken()
            val service = Sheets.Builder(NetHttpTransport(), jsonFactory, credential)
            val response = service.build().spreadsheets().values()
                .get(spreadsheetId, range).execute()
            return response.getValues()
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

}