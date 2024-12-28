package com.intake.intakevisor.analyse

import android.graphics.Bitmap
import android.graphics.Rect

class FoodProcessor(val frame: Frame) : FoodDetector {
    override fun detectFoods(): List<FoodRegion> {
        val regions = listOf(
            Rect(100, 100, 300, 300),
            Rect(500, 500, 800, 800),
            Rect(300, 1000, 600, 1300)
        )

        // Extract fragments from the frame based on the regions
        val foodRegions = regions.map { rect ->
            val fragment = cropBitmap(frame.image as Bitmap, rect)
            FoodRegion(rect, fragment, NutritionInfo("Sample Food", 100, 50))
        }

        return foodRegions
    }

    private fun cropBitmap(source: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(
            source,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
    }
}
