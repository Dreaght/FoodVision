package com.intake.intakevisor.api.request

import com.squareup.moshi.Json

data class ReportResponse(
    @Json(name = "result") val result: String
)
