package com.intake.intakevisor.analyse

interface FoodDetector {
    suspend fun detectFoods(): List<FoodRegion>
}