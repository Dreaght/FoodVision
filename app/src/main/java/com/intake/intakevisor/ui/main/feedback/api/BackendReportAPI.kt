package com.intake.intakevisor.ui.main.feedback.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.api.request.ReportRequest
import com.intake.intakevisor.ui.main.diary.DiaryDatabaseHelper
import com.intake.intakevisor.ui.main.diary.FoodItem
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange
import com.intake.intakevisor.ui.main.feedback.api.model.InputReportData
import com.intake.intakevisor.ui.welcome.UserData
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class BackendReportAPI : ReportAPI {

    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper
    private val api = RetrofitClient.api
    private val moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter(InputReportData::class.java)

    var userData = UserData()

    override suspend fun fetchReport(range: ReportDaysRange, context: Context): Bitmap {
        loadUserData(context)

        diaryDatabaseHelper = DiaryDatabaseHelper(context)

        val foodItems = getNutritionInfoFromDatabase(range)

        Log.d("BackendReportAPI", "Food items: $foodItems")

        val reportData = InputReportData.of(foodItems, userData)
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

    private fun loadUserData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

        userData.gender = sharedPreferences.getString("gender", userData.gender)!!
        userData.weight = sharedPreferences.getInt("weight", userData.weight)
        userData.height = sharedPreferences.getInt("height", userData.height)
        userData.age = sharedPreferences.getInt("age", userData.age)
        userData.goalWeight = sharedPreferences.getInt("goalWeight", userData.goalWeight)
        userData.birthDate = sharedPreferences.getString("birthDate", userData.birthDate)!!
    }
}
