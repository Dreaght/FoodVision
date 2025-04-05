package com.intake.intakevisor.db

import android.util.Log

class LocalDiaryDatabaseImpl(private val diaryDao: DiaryDao) : DiaryDatabase {
    // Insert a FoodFragmentEntity
    override suspend fun insertFoodFragment(date: String, mealType: String, food: FoodFragmentEntity) {
        diaryDao.insertFoodFragment(food) // Save the returned ID if needed
    }

    // Get food items for a meal and return FoodFragmentEntity
    override suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodFragmentEntity> {
        return diaryDao.getFoodItemsForMeal(date, mealType) // Directly return FoodFragmentEntity list
    }

    override suspend fun deleteFoodFragmentById(id: Long) {
        diaryDao.deleteFoodFragmentById(id)
    }

    override suspend fun hasDataForDate(date: String): Boolean {
        return diaryDao.hasDataForDate(date)
    }

    override suspend fun clearFoodFragments() {
        diaryDao.clearFoodFragments()
    }
}
