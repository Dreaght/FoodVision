package com.intake.intakevisor.analyse

interface FoodDetector {
    fun detectFoodRegions(): List<FoodRegion>
}