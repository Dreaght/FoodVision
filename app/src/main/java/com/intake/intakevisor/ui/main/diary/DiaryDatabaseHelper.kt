package com.intake.intakevisor.ui.main.diary

import android.content.Context
import android.graphics.BitmapFactory
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.db.DiaryDatabase
import com.intake.intakevisor.db.FoodFragmentEntity
import com.intake.intakevisor.db.LocalDiaryDatabase
import com.intake.intakevisor.db.LocalDiaryDatabaseImpl
import com.intake.intakevisor.ui.main.feedback.ReportDaysRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DiaryDatabaseHelper(context: Context) {
    private val db = LocalDiaryDatabase.getDatabase(context)
    private val localDiaryDatabase: DiaryDatabase = LocalDiaryDatabaseImpl(db.diaryDao())
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()  // Ensure it uses the local timezone
    }

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

    suspend fun getMealsForDaysRange(range: ReportDaysRange): List<Map<String, List<FoodItem>>> {
        val meals = mutableListOf<Map<String, List<FoodItem>>>()
        var currentDay = range.start

        // Loop until the end date is reached
        while (currentDay.before(range.end) || currentDay == range.end) {
            val formattedDate = dateFormatter.format(currentDay.time)
            meals.add(getMealsForDate(formattedDate))

            // Increment the day by 1
            currentDay.add(Calendar.DAY_OF_MONTH, 1)
        }

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

    // New method to reset the database
    fun resetDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Resetting all data by clearing the tables
            localDiaryDatabase.clearFoodFragments()
            // Optionally, you can add more tables to clear if needed
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
            nutrition = foodFragment.nutritionInfo
        )
    }

    fun createFoodItem(entity: FoodFragmentEntity): FoodItem {
        val bitmap = BitmapFactory.decodeByteArray(entity.image, 0, entity.image.size)
        val nutritionInfo = entity.nutrition
        return FoodItem(
            nutrition = nutritionInfo,
            image = bitmap
        )
    }
}
