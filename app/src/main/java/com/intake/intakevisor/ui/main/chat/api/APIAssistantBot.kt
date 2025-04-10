package com.intake.intakevisor.ui.main.chat.api

import android.content.Context
import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.ui.main.diary.DiaryDatabaseHelper
import com.intake.intakevisor.ui.main.diary.FoodItem
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange
import com.intake.intakevisor.ui.main.feedback.api.model.InputReportData
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class APIAssistantBot : AssistantBot {

    private val api = RetrofitClient.api
    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper

    private val moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter(InputReportData::class.java)

    override suspend fun getResponseFragments(
        message: String,
        context: Context,
        onFragmentReceived: (String) -> Unit
    ) {
        diaryDatabaseHelper = DiaryDatabaseHelper(context)

        val todayLocalDate = LocalDate.now()
        val weekAgoLocalDate = todayLocalDate.minusDays(7)

        val todayCalendar = Calendar.getInstance()
todayCalendar.time = java.util.Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

val weekAgoCalendar = Calendar.getInstance()
weekAgoCalendar.time = java.util.Date.from(weekAgoLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // Get the food items
        val foodItems = getNutritionInfoFromDatabase(ReportDaysRange(weekAgoCalendar, todayCalendar))
        val reportData = InputReportData.of(foodItems)
        val jsonString = jsonAdapter.toJson(reportData)

        // Send the request
        val responseBody: ResponseBody = api.sendMessage("""
            {"message": $message, "data": $jsonString}
        """.trimIndent())

        flow {
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                emit(line!!)
            }
        }
            .flowOn(Dispatchers.IO) // Process on IO thread
            .collect { fragment ->
                onFragmentReceived(fragment)
            }
    }


    private suspend fun getNutritionInfoFromDatabase(range: ReportDaysRange): List<Map<String, List<FoodItem>>> {
        return diaryDatabaseHelper.getMealsForDaysRange(range)
    }
}
