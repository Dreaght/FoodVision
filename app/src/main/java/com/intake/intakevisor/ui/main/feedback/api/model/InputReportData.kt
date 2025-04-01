package com.intake.intakevisor.ui.main.feedback.api.model

import com.intake.intakevisor.analyse.NutritionInfo
import com.intake.intakevisor.ui.main.diary.FoodItem

data class InputReportData(
    val data: List<Map<String, List<NutritionInfo>>>
) {
    companion object {
        fun empty(): InputReportData {
            return InputReportData(emptyList())
        }

        fun of(data: List<Map<String, List<FoodItem>>>): InputReportData {
            val transformedData = data.map { map ->
                map.mapValues {
                    it.value.map {
                        foodItem -> foodItem.nutrition
                    }
                }
            }
            return InputReportData(transformedData)
        }
    }
}