package com.intake.intakevisor.ui.main.chat.api

import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.api.request.ChatRequest
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

class APIAssistantBot : AssistantBot {

    private val api = RetrofitClient.api
    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper

    private val moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter(InputReportData::class.java)

    override suspend fun getResponseFragments(
        message: String,
        onFragmentReceived: (String) -> Unit
    ) {

        val today = LocalDate.now()
        val weekAgo = today.minusDays(7)

        val foodItems = getNutritionInfoFromDatabase(ReportDaysRange(weekAgo, today))
        val reportData = InputReportData.of(foodItems)
        val jsonString = jsonAdapter.toJson(reportData)

        val responseBody: ResponseBody = api.sendMessage(ChatRequest("$message $jsonString"))

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
