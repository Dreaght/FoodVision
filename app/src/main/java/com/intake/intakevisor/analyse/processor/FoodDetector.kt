package com.intake.intakevisor.analyse.processor

import com.intake.intakevisor.analyse.FoodRegion

interface FoodDetector {
    suspend fun detectFoods(): List<FoodRegion>
}