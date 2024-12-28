package com.intake.intakevisor.analyse

interface FoodDetector {
    fun detectFoods(): List<FoodRegion>
}