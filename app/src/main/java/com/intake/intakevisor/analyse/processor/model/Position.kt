package com.intake.intakevisor.analyse.processor.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Position(val X: Double, val Y: Double)
