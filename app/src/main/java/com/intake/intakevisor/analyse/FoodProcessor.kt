package com.intake.intakevisor.analyse

class FoodProcessor : FoodDetector, NutritionAnalyzer {
    override fun detectFoodRegions(frame: Frame): List<FoodRegion> {
        // TODO: Implement food region detection using OpenCV
        return emptyList()
    }

    override fun analyze(foodRegion: FoodRegion): NutritionInfo {
        // TODO: Implement nutrition analysis (e.g., querying GPT)
        return NutritionInfo("", 0, 0)
    }
}