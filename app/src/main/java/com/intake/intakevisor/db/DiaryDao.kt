package com.intake.intakevisor.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {

    @Insert
    suspend fun insertFoodFragment(foodFragmentEntity: FoodFragmentEntity): Long

    @Query("SELECT * FROM food_fragments WHERE date = :date AND mealType = :mealType")
    suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodFragmentEntity>

    @Query("DELETE FROM food_fragments WHERE id = :id")
    suspend fun deleteFoodFragmentById(id: Long)

    @Query("SELECT COUNT(*) > 0 FROM food_fragments WHERE date = :date")
    suspend fun hasDataForDate(date: String): Boolean

    @Query("DELETE FROM food_fragments")
    suspend fun clearFoodFragments()
}
