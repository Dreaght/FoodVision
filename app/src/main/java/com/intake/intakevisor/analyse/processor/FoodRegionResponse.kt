package com.intake.intakevisor.analyse.processor

data class FoodRegionResponse(
    val pos_start: Position,
    val pos_end: Position,
    val name: String,
    val nutrition: NutritionInfo
)

data class Position(
    val X: Int,
    val Y: Int
)

data class NutritionInfo(
    val calories: Amount,
    val transFat: Amount,
    val saturatedFat: Amount,
    val totalFat: Amount,
    val protein: Amount,
    val sugar: Amount,
    val cholesteral: Amount,
    val sodium: Amount,
    val minerals: Minerals,
    val vitamins: Vitamins
)

data class Amount(
    val amount: Double,
    val units: Units
)

data class Units(
    val full: String,
    val short: String
)

data class Minerals(
    val calcium: Amount,
    val iodine: Amount,
    val iron: Amount,
    val magnesium: Amount,
    val potassium: Amount,
    val zinc: Amount
)

data class Vitamins(
    val vitaminA: Amount,
    val vitaminC: Amount,
    val vitaminD: Amount,
    val vitaminE: Amount,
    val vitaminK: Amount,
    val vitaminB1: Amount,
    val vitaminB2: Amount,
    val vitaminB3: Amount,
    val vitaminB5: Amount,
    val vitaminB6: Amount,
    val vitaminB7: Amount,
    val vitaminB9: Amount,
    val vitaminB12: Amount
)
