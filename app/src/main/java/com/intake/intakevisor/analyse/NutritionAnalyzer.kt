package com.intake.intakevisor.analyse

interface NutritionAnalyzer {
    fun analyze(foodRegion: FoodRegion): NutritionInfo
}