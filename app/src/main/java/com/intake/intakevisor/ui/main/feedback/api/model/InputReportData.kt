package com.intake.intakevisor.ui.main.feedback.api.model

import com.intake.intakevisor.analyse.NutritionInfo
import com.intake.intakevisor.ui.main.diary.FoodItem
import com.intake.intakevisor.ui.welcome.UserData
import java.time.LocalDate

data class InputReportData(
    val data: List<ReportPage>,
    val userData: UserData
) {
    companion object {
        fun empty(): InputReportData {
            return InputReportData(emptyList(), UserData())
        }

        fun of(data: List<Map<String, List<FoodItem>>>, userData: UserData): InputReportData {
            val reportPages = data.map { map ->
                val pageData = map.mapValues { entry ->
                    entry.value.map { foodItem -> foodItem.nutrition }
                }
                ReportPage(pageData, LocalDate.now().toString())
            }
            return InputReportData(reportPages, userData)
        }
    }
}

data class ReportPage(
    val pageData: Map<String, List<NutritionInfo>>,
    val date: String
)
