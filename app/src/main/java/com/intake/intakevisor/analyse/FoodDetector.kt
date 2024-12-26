package com.intake.intakevisor.analyse

interface FoodDetector {
    fun detectFoodRegions(frame: Frame): List<FoodRegion>
}