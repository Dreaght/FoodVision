package com.intake.intakevisor.ui.main.feedback.api

import android.content.Context
import android.graphics.Bitmap
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange

interface ReportAPI {
    suspend fun fetchReport(range: ReportDaysRange, context: Context): Bitmap
}