package com.intake.intakevisor.ui.main.feedback.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.api.request.ReportRequest
import com.intake.intakevisor.ui.main.diary.DiaryDatabaseHelper
import com.intake.intakevisor.ui.main.diary.FoodItem
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange
import com.intake.intakevisor.ui.main.feedback.api.model.InputReportData
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InputStream

class BackendReportAPI : ReportAPI {

    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper
    private val api = RetrofitClient.api
    private val moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter(InputReportData::class.java)

    override suspend fun fetchReport(range: ReportDaysRange, context: Context): Bitmap {
        diaryDatabaseHelper = DiaryDatabaseHelper(context)

        val foodItems = getNutritionInfoFromDatabase(range)
        val reportData = InputReportData.of(foodItems)
        val jsonString = jsonAdapter.toJson(reportData)

        val response: Response<ResponseBody> = api.sendReport(jsonString)

        if (response.isSuccessful && response.body() != null) {
            return convertResponseToBitmap(response.body()!!.byteStream())
        } else {
            throw Exception("Failed to fetch report: ${response.errorBody()?.string()}")
        }
    }

    private suspend fun getNutritionInfoFromDatabase(range: ReportDaysRange): List<Map<String, List<FoodItem>>> {
        return diaryDatabaseHelper.getMealsForDaysRange(range)
    }

    private fun convertResponseToBitmap(inputStream: InputStream): Bitmap {
        return BitmapFactory.decodeStream(inputStream)
    }
}
