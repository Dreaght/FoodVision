package com.intake.intakevisor.api.request

import com.squareup.moshi.Json

data class ChatResponse(
    @Json(name = "reply") val reply: String
)
