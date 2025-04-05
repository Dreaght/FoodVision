package com.intake.intakevisor.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertFoodFragment(foodFragmentEntity: FoodFragmentEntity)

    @Query("SELECT * FROM food_fragments WHERE date = :date AND mealType = :mealType")
    suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodFragmentEntity>

    @Delete
    fun deleteFoodFragment(foodFragmentEntity: FoodFragmentEntity)

    @Query("SELECT COUNT(*) > 0 FROM food_fragments WHERE date = :date")
    suspend fun hasDataForDate(date: String): Boolean

    @Query("DELETE FROM food_fragments")
    suspend fun clearFoodFragments()
}
