package com.intake.intakevisor.db

class LocalDiaryDatabaseImpl(private val diaryDao: DiaryDao) : DiaryDatabase {
    // Insert a FoodFragmentEntity
    override suspend fun insertFoodFragment(date: String, mealType: String, food: FoodFragmentEntity) {
        diaryDao.insertFoodFragment(food) // Directly use FoodFragmentEntity here
    }

    // Get food items for a meal and return FoodFragmentEntity
    override suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodFragmentEntity> {
        return diaryDao.getFoodItemsForMeal(date, mealType) // Directly return FoodFragmentEntity list
    }

    // Delete a FoodFragmentEntity
    override suspend fun deleteFoodFragment(date: String, mealType: String, food: FoodFragmentEntity) {
        diaryDao.deleteFoodFragment(food) // Directly use FoodFragmentEntity here
    }

    override suspend fun hasDataForDate(date: String): Boolean {
        return diaryDao.hasDataForDate(date)
    }

    override suspend fun clearFoodFragments() {
        diaryDao.clearFoodFragments()
    }
}
