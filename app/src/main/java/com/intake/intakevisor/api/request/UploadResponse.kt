package com.intake.intakevisor.api.request

import com.intake.intakevisor.analyse.processor.model.FoodFragmentAPI
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "regions") val regions: List<FoodFragmentAPI>
)
