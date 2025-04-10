package com.intake.intakevisor.analyse.processor.model

import com.intake.intakevisor.analyse.NutritionInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodFragmentAPI(
    val start: Position,
    val end: Position,
    val nutrition: NutritionInfo
)
