package com.intake.intakevisor.db

interface DiaryDatabase {
    suspend fun insertFoodFragment(date: String, mealType: String, food: FoodFragmentEntity)
    suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodFragmentEntity>
    suspend fun deleteFoodFragment(date: String, mealType: String, food: FoodFragmentEntity)
    suspend fun hasDataForDate(date: String): Boolean
    suspend fun clearFoodFragments()
}
