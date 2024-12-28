package com.intake.intakevisor.analyse

import android.graphics.Rect

class FoodProcessor(frame: Frame) : FoodDetector, NutritionAnalyzer {
    override fun detectFoodRegions(): List<FoodRegion> {
        // TODO: Implement food region detection using OpenCV
        return listOf(
            FoodRegion(Rect(100, 100, 300, 300)),
            FoodRegion(Rect(500, 500, 800, 800)),
            FoodRegion(Rect(300, 1000, 600, 1300))
        )
    }

    override fun analyze(foodRegion: FoodRegion): NutritionInfo {
        // TODO: Implement nutrition analysis (e.g., querying GPT)
        return NutritionInfo("", 0, 0)
    }
}