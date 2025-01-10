package com.intake.intakevisor.ui.main.diary

import android.content.Context
import android.graphics.BitmapFactory
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.analyse.NutritionInfo
import com.intake.intakevisor.db.DiaryDatabase
import com.intake.intakevisor.db.FoodFragmentEntity
import com.intake.intakevisor.db.LocalDiaryDatabase
import com.intake.intakevisor.db.LocalDiaryDatabaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryDatabaseHelper(context: Context) {
    private val db = LocalDiaryDatabase.getDatabase(context)
    private val localDiaryDatabase: DiaryDatabase = LocalDiaryDatabaseImpl(db.diaryDao())

    suspend fun hasDataForDate(formattedDate: String): Boolean {
        return localDiaryDatabase.hasDataForDate(formattedDate)
    }

    suspend fun getFoodItemsForMeal(formattedDate: String, mealType: String) : List<FoodFragmentEntity> {
        return localDiaryDatabase.getFoodItemsForMeal(formattedDate, mealType)
    }

    suspend fun getMealsForDate(formattedDate: String): Map<String, List<FoodItem>> {
        val meals = mutableMapOf<String, List<FoodItem>>()
        meals.putAll(
            getFoodItemsForMeal(formattedDate, "breakfast").map { it.mealType to listOf(createFoodItem(it)) }
        )
        meals.putAll(
            getFoodItemsForMeal(formattedDate, "lunch").map { it.mealType to listOf(createFoodItem(it)) }
        )
        meals.putAll(
            getFoodItemsForMeal(formattedDate, "dinner").map { it.mealType to listOf(createFoodItem(it)) }
        )

        return meals
    }

    fun saveFoodFragmentToDatabase(
        selectedDate: String, mealType: String, foodFragment: FoodFragment) {
        CoroutineScope(Dispatchers.IO).launch {
            // Insert food fragment into the database
            localDiaryDatabase.insertFoodFragment(
                selectedDate, mealType, convertFoodFragmentToFragmentEntity(
                    selectedDate, mealType, foodFragment))
        }
    }

    fun deleteFoodFragmentFromDatabase(
        selectedDate: String, mealType: String, foodFragment: FoodFragment)
    {
        CoroutineScope(Dispatchers.IO).launch {
            // Delete food fragment from the database
            localDiaryDatabase.deleteFoodFragment(
                selectedDate, mealType, convertFoodFragmentToFragmentEntity(
                    selectedDate, mealType, foodFragment))
        }
    }

    fun convertFoodFragmentToFragmentEntity(
        selectedDate: String, mealType: String, foodFragment: FoodFragment): FoodFragmentEntity
    {
        return FoodFragmentEntity(
            dateMealTypeKey = "$selectedDate-$mealType",
            date = selectedDate,
            mealType = mealType,
            image = foodFragment.image,
            name = foodFragment.nutritionInfo.name,
            calories = foodFragment.nutritionInfo.calories
        )
    }

    fun createFoodItem(entity: FoodFragmentEntity): FoodItem {
        val bitmap = BitmapFactory.decodeByteArray(entity.image, 0, entity.image.size)
        val nutritionInfo = NutritionInfo(name = entity.name, calories = entity.calories)
        return FoodItem(
            name = "${nutritionInfo.name} ${nutritionInfo.calories}",
            image = bitmap
        )
    }
}
