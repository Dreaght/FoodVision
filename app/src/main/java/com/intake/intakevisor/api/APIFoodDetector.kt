package com.intake.intakevisor.api

import com.intake.intakevisor.analyse.FoodDetector
import com.intake.intakevisor.analyse.FoodRegion

class APIFoodDetector : FoodDetector {
    override suspend fun detectFoods(): List<FoodRegion> {
        TODO("Not yet implemented")
    }
}
