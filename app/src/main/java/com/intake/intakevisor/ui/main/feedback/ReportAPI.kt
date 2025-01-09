package com.intake.intakevisor.ui.main.feedback

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlinx.coroutines.delay

class ReportAPI {
    // Simulate an API that generates a bitmap image of a report
    suspend fun fetchReport(range: ReportDaysRange): Bitmap {
        delay(3000) // Simulate a 3-second loading time
        return generateDummyReportImage(range)
    }

    private fun generateDummyReportImage(range: ReportDaysRange): Bitmap {
        val width = 800
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Text
        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText("Nutrition Report", 100f, 100f, paint)
        canvas.drawText("Date Range: ${range.start} - ${range.end}", 100f, 200f, paint)
        canvas.drawText("Carbs: 50%", 100f, 300f, paint)
        canvas.drawText("Fats: 30%", 100f, 400f, paint)
        canvas.drawText("Proteins: 20%", 100f, 500f, paint)

        return bitmap
    }
}
