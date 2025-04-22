package com.intake.intakevisor.ui.main.chat.api

import android.content.Context
import android.util.Log
import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.ui.main.diary.DiaryDatabaseHelper
import com.intake.intakevisor.ui.main.diary.FoodItem
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange
import com.intake.intakevisor.ui.main.feedback.api.model.InputReportData
import com.intake.intakevisor.ui.welcome.UserData
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
    private val jsonAdapter = moshi.adapter(ChatInputData::class.java)

    var userData = UserData()

    override suspend fun getResponseFragments(
        message: String,
        context: Context,
        onFragmentReceived: (String) -> Unit
    ) {
        loadUserData(context)

        diaryDatabaseHelper = DiaryDatabaseHelper(context)

        val todayLocalDate = LocalDate.now()
        val weekAgoLocalDate = todayLocalDate.minusDays(7)

        val todayCalendar = Calendar.getInstance()
todayCalendar.time = java.util.Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

val weekAgoCalendar = Calendar.getInstance()
weekAgoCalendar.time = java.util.Date.from(weekAgoLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // Get the food items
        val foodItems = getNutritionInfoFromDatabase(ReportDaysRange(weekAgoCalendar, todayCalendar))
        val reportData = ChatInputData(message, InputReportData.of(foodItems, userData).data, userData)
        val jsonString = jsonAdapter.toJson(reportData)

        // Send the request
        val responseBody: ResponseBody = api.sendMessage(jsonString.trimIndent())

        Log.d("APIAssistantBot", "Response body: $responseBody")

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
