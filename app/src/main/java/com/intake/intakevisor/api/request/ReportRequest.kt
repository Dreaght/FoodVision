package com.intake.intakevisor.api.request

import com.squareup.moshi.Json

data class ReportRequest(
    @Json(name = "data") val data: String
)
