package com.intake.intakevisor.api.request

import com.squareup.moshi.Json

data class UploadResponse(
    @Json(name = "message") val message: String
)
