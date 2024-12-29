package com.intake.intakevisor

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.analyse.NutritionInfo
import com.intake.intakevisor.api.DiaryDatabase
import com.intake.intakevisor.api.FoodFragmentEntity
import com.intake.intakevisor.api.LocalDiaryDatabase
import com.intake.intakevisor.api.LocalDiaryDatabaseImpl
import com.intake.intakevisor.diary.FoodItem
import com.intake.intakevisor.diary.FoodItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DiaryActivity : AppCompatActivity() {

    private lateinit var localDiaryDatabase: DiaryDatabase

    private lateinit var currentDayTextView: TextView
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val currentDay: Calendar = Calendar.getInstance()

    private val breakfastItems = mutableListOf<FoodItem>()
    private val lunchItems = mutableListOf<FoodItem>()
    private val dinnerItems = mutableListOf<FoodItem>()

    private var currentJob: Job? = null

    val sessionFoodFragments = mutableMapOf<String, MutableList<FoodFragment>>()

    private val mealTypeMap = mapOf(
        "breakfast" to breakfastItems,
        "lunch" to lunchItems,
        "dinner" to dinnerItems
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Initialize UI components
        currentDayTextView = findViewById(R.id.currentDay)
        updateCurrentDayLabel()

        findViewById<Button>(R.id.previousDay).setOnClickListener {
            changeDay(-1)
        }

        findViewById<Button>(R.id.nextDay).setOnClickListener {
            changeDay(1)
        }

        // Initialize RecyclerViews
        setupRecyclerView(R.id.breakfastFoodList, breakfastItems)
        setupRecyclerView(R.id.lunchFoodList, lunchItems)
        setupRecyclerView(R.id.dinnerFoodList, dinnerItems)

        findViewById<Button>(R.id.addBreakfast).setOnClickListener {
            openCameraActivity("breakfast")
            refreshFoodData()
        }
        findViewById<Button>(R.id.addLunch).setOnClickListener {
            openCameraActivity("lunch")
            refreshFoodData()
        }
        findViewById<Button>(R.id.addDinner).setOnClickListener {
            openCameraActivity("dinner")
            refreshFoodData()
        }

        val db = LocalDiaryDatabase.getDatabase(applicationContext)
        localDiaryDatabase = LocalDiaryDatabaseImpl(db.diaryDao())

        handleFoodFragments()

        val selectedDate = intent.getStringExtra("selected_date")
        if (selectedDate != null) {
            setDayFromSelectedDate(selectedDate)
        }

//        changeDay(0)
        refreshFoodData()
    }

    private fun setDayFromSelectedDate(selectedDate: String) {
        try {
            // Parse the selected_date string into a Date object
            val parsedDate = dateFormatter.parse(selectedDate) ?: return

            // Update the currentDay Calendar instance
            currentDay.time = parsedDate

            // Update the UI and load data for the selected date
            updateCurrentDayLabel()
            refreshFoodData()
        } catch (e: Exception) {
            Log.e("DiaryActivity", "Error parsing selected_date: $selectedDate", e)
        }
    }

    private fun changeDay(offset: Int) {
        val today = Calendar.getInstance()
        val cutoffDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }
        val targetDay = (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }

        // Prevent navigating to future dates
        if (targetDay.after(today)) {
            return
        }

        val allowNavigation = runBlocking {
            if (targetDay.before(cutoffDate)) {
                for (i in 0 until 3) {
                    val dayToCheck = (targetDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i + 1) }
                    val formattedDate = dateFormatter.format(dayToCheck.time)
                    if (localDiaryDatabase.hasDataForDate(formattedDate)) {
                        return@runBlocking true
                    }
                }
                return@runBlocking false
            }
            true
        }

        if (!allowNavigation) {
            return
        }

        // Update the current day and refresh the UI
        currentDay.time = targetDay.time
        updateCurrentDayLabel()
        refreshFoodData()
    }

    private fun updateCurrentDayLabel() {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        currentDayTextView.text = when {
            isSameDay(currentDay, today) -> getString(R.string.today)
            isSameDay(currentDay, yesterday) -> getString(R.string.yesterday)
            else -> dateFormatter.format(currentDay.time)
        }
    }

    private fun refreshFoodData() {
        // Clear existing data
        mealTypeMap.values.forEach { it.clear() }

        // Notify the adapters that the data set has changed
        runOnUiThread {
            findRecyclerView("breakfast").adapter?.notifyDataSetChanged()
            findRecyclerView("lunch").adapter?.notifyDataSetChanged()
            findRecyclerView("dinner").adapter?.notifyDataSetChanged()
        }

        // Load data for the new day
        initializeFromDatabase()
    }

    private fun initializeFromDatabase() {
        currentJob?.cancel() // Cancel the previous job
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            val formattedDate = dateFormatter.format(currentDay.time)

            mealTypeMap.forEach { (mealType, foodList) ->
                val foodItems = fetchFoodItemsForMeal(formattedDate, mealType)

                withContext(Dispatchers.Main) {
                    populateFoodListFromDatabase(foodList, foodItems, mealType)
                }
            }
        }
    }

    private suspend fun fetchFoodItemsForMeal(formattedDate: String, mealType: String): List<FoodFragmentEntity> {
        val foodItems = localDiaryDatabase.getFoodItemsForMeal(formattedDate, mealType)
        return foodItems
    }

    private fun populateFoodListFromDatabase(foodList: MutableList<FoodItem>, foodItems: List<FoodFragmentEntity>, mealType: String) {
        val initialDate = dateFormatter.format(currentDay.time)
        foodItems.forEach { foodFragmentEntity ->
            if (foodFragmentEntity.date != initialDate) return@forEach // Skip items that don't match the current day

            val foodFragment = convertToFoodFragment(foodFragmentEntity)
            foodList.add(createFoodItem(foodFragment))
        }

        // Notify the adapter
        findRecyclerView(mealType).adapter?.notifyDataSetChanged()
    }

    private fun convertToFoodFragment(entity: FoodFragmentEntity): FoodFragment {
        return FoodFragment(
            image = entity.image, // assuming image is a ByteArray or something that can be converted to Bitmap
            nutritionInfo = NutritionInfo(name = entity.name, calories = entity.calories)
        )
    }

    private fun openCameraActivity(mealType: String) {
        val uniqueSessionId = System.currentTimeMillis().toString()
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("meal_type", mealType)
        intent.putExtra("selected_date", dateFormatter.format(currentDay.time)) // Pass formatted date
        intent.putExtra("session_id", uniqueSessionId)

        sessionFoodFragments.put(uniqueSessionId, mutableListOf())

        startActivity(intent)
    }

    private fun handleFoodFragments() {
        val foodFragments = retrieveFoodFragmentsFromIntent() // Assuming this can return null
        val mealType = intent.getStringExtra("meal_type") ?: return
        val selectedDate = intent.getStringExtra("selected_date") ?: return
        val sessionId = intent.getStringExtra("session_id") ?: return  // Ensures sessionId is not null

        // Check if foodFragments is not null before proceeding
        foodFragments?.forEach { foodFragment ->
            // Ensure sessionId is not null and that the session exists in the map
            // Coroutine to check and update the database asynchronously
                val isAdded = sessionFoodFragments[sessionId]?.contains(foodFragment) == true

                // Add to database if not already added
                if (!isAdded) {
                    saveFoodFragmentToDatabase(selectedDate, mealType, foodFragment)

                    sessionFoodFragments[sessionId]?.add(foodFragment) ?: run {
                        // If the session doesn't exist, create the list and add the food fragment
                        sessionFoodFragments[sessionId] = mutableListOf(foodFragment)
                    }
                } else {
                    // Update or remove logic can go here if necessary
                }
        }

        // Now handle deletion of fragments from the session
        sessionFoodFragments[sessionId]?.forEach { fragment ->
            // If the fragment is not in the foodFragments list, delete it
            if (foodFragments?.contains(fragment) == false) {
                deleteFoodFragmentFromDatabase(selectedDate, mealType, fragment)
            }
        }
    }

    private fun saveFoodFragmentToDatabase(
        selectedDate: String, mealType: String, foodFragment: FoodFragment) {
        CoroutineScope(Dispatchers.IO).launch {
            localDiaryDatabase.insertFoodFragment(
                selectedDate, mealType, convertFoodFragmentToFragmentEntity(
                    selectedDate, mealType, foodFragment))
        }
    }

    private fun deleteFoodFragmentFromDatabase(
        selectedDate: String, mealType: String, foodFragment: FoodFragment)
    {
        CoroutineScope(Dispatchers.IO).launch {
            localDiaryDatabase.deleteFoodFragment(
                selectedDate, mealType, convertFoodFragmentToFragmentEntity(
                    selectedDate, mealType, foodFragment))
        }
    }

    private fun convertFoodFragmentToFragmentEntity(
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

    private fun addFoodItemToMeal(mealType: String, foodItem: FoodItem) {
        mealTypeMap[mealType]?.add(foodItem)
        findRecyclerView(mealType).adapter?.notifyItemInserted(mealTypeMap[mealType]?.size?.minus(1) ?: 0)
    }

    private fun retrieveFoodFragmentsFromIntent(): ArrayList<FoodFragment>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("food_fragments", FoodFragment::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("food_fragments")
        }
    }

    private fun setupRecyclerView(recyclerViewId: Int, foodItems: MutableList<FoodItem>) {
        val recyclerView: RecyclerView = findViewById(recyclerViewId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FoodItemAdapter(foodItems)
    }

    private fun findRecyclerView(mealType: String): RecyclerView {
        return when (mealType) {
            "breakfast" -> findViewById(R.id.breakfastFoodList)
            "lunch" -> findViewById(R.id.lunchFoodList)
            "dinner" -> findViewById(R.id.dinnerFoodList)
            else -> throw IllegalArgumentException("Unknown meal type: $mealType")
        }
    }

    private fun createFoodItem(foodFragment: FoodFragment): FoodItem {
        val bitmap = BitmapFactory.decodeByteArray(foodFragment.image, 0, foodFragment.image.size)
        val nutritionInfo = foodFragment.nutritionInfo
        return FoodItem(
            name = "${nutritionInfo.name} ${nutritionInfo.calories}", // Use the name from the FoodFragmentEntity
            image = bitmap // Use the bitmap image
        )
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
