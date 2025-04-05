package com.intake.intakevisor.ui.main.diary

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.CameraActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.databinding.DiaryFragmentBinding
import com.intake.intakevisor.db.FoodFragmentEntity
import com.intake.intakevisor.listener.SimpleItemTouchHelperCallback
import com.intake.intakevisor.listener.SwipeToDeleteCallback
import com.intake.intakevisor.ui.main.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {

    private var _binding: DiaryFragmentBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private lateinit var mainActivity: MainActivity
    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()  // Ensure it uses the local timezone
    }
    private val currentDay: Calendar = Calendar.getInstance()
    private val mealData = mapOf(
        "breakfast" to mutableListOf<FoodItem>(),
        "lunch" to mutableListOf<FoodItem>(),
        "dinner" to mutableListOf<FoodItem>()
    )
    private var currentJob: Job? = null

    private lateinit var cameraActivityLauncher: ActivityResultLauncher<Intent>

    private val cachedData = mutableMapOf<Calendar, Map<String, List<FoodItem>>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DiaryFragmentBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as? MainActivity
            ?: throw IllegalStateException("Activity must be MainActivity")
        mainActivity.activateItemInMenu(this)

        diaryDatabaseHelper = DiaryDatabaseHelper(requireContext())

        initializeCameraActivityLauncher()
        setupUI()
        loadFoodDataFromDatabase()
        changeDay(0)
    }

    private fun initializeCameraActivityLauncher() {
        cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val mealType = result.data?.getStringExtra("meal_type") ?: return@registerForActivityResult
                val foodFragments = result.data?.getParcelableArrayListExtra<FoodFragment>("food_fragments") ?: return@registerForActivityResult

                foodFragments.forEach { foodFragment ->
                    diaryDatabaseHelper.saveFoodFragmentToDatabase(
                        dateFormatter.format(currentDay.time),
                        mealType,
                        foodFragment
                    )
                }
                refreshFoodData() // Refresh UI after adding data
                changeDay(0)
            }
        }
    }

    private fun setupUI() {
        applyInsetsToDaySwitcher()
        setupDayNavigation()
        setupMealRecyclerViews()
        setupAddMealButtons()
    }

    private fun applyInsetsToDaySwitcher() {
        _binding?.daySwitcher?.let { daySwitcher ->
            ViewCompat.setOnApplyWindowInsetsListener(daySwitcher) { view, insets ->
                val marginTop = maxOf(
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                    insets.displayCutout?.safeInsetTop ?: 0,
                    dpToPx(20)
                )
                view.updateLayoutParams<ConstraintLayout.LayoutParams> { topMargin = marginTop }
                insets
            }
        }
    }

    private fun setupDayNavigation() {
        _binding?.previousDay?.setOnClickListener { changeDay(-1) }
        _binding?.nextDay?.setOnClickListener { changeDay(1) }
    }

    private fun setupMealRecyclerViews() {
        mealData.forEach { (mealType, items) ->
            val recyclerView = findRecyclerViewSafe(mealType) ?: return@forEach
            recyclerView.layoutManager = LinearLayoutManager(mainActivity)
            recyclerView.adapter = FoodItemAdapter(items) { foodItem ->
                deleteFoodFragment(foodItem, mealType)
            }

            val foodItemAdapter = recyclerView.adapter as FoodItemAdapter
            val itemTouchHelper = SimpleItemTouchHelperCallback(foodItemAdapter)
            val touchHelper = ItemTouchHelper(itemTouchHelper)
            touchHelper.attachToRecyclerView(findRecyclerViewSafe(mealType))
        }
    }

    private fun setupAddMealButtons() {
        _binding?.apply {
            addBreakfast.setOnClickListener { addMeal("breakfast") }
            addLunch.setOnClickListener { addMeal("lunch") }
            addDinner.setOnClickListener { addMeal("dinner") }
        }
    }

    private fun addMeal(mealType: String) {
        val intent = Intent(mainActivity, CameraActivity::class.java).apply {
            putExtra("meal_type", mealType)
            putExtra("selected_date", dateFormatter.format(currentDay.time))
        }
        cameraActivityLauncher.launch(intent)
        refreshFoodData()
        changeDay(0)
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    private fun updateCurrentDayLabel() {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        _binding?.currentDay?.text = when {
            isSameDay(currentDay, today) -> getString(R.string.today)
            isSameDay(currentDay, yesterday) -> getString(R.string.yesterday)
            else -> dateFormatter.format(currentDay.time)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun preloadDayData() {
        val previousDay = (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
        val nextDay = (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }

        CoroutineScope(Dispatchers.IO).launch {
            listOf(previousDay, nextDay).forEach { day ->
                val formattedDate = dateFormatter.format(day.time)
                val meals = diaryDatabaseHelper.getMealsForDate(formattedDate)
                cachedData[day] = meals
            }
        }
    }

    private fun changeDay(offset: Int) {
        val targetDay = (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }

        if (targetDay.after(Calendar.getInstance())) return

        currentJob?.cancel()
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            if (!validateDayNavigation(targetDay)) return@launch

            withContext(Dispatchers.Main) {
                currentDay.time = targetDay.time
                updateCurrentDayLabel()

                val cachedMeals = cachedData[targetDay]
                if (cachedMeals != null) {
                    mealData.forEach { (mealType, foodList) ->
                        foodList.clear()
                        foodList.addAll(cachedMeals[mealType] ?: emptyList())
                    }
                    mealData.keys.forEach { mealType ->
                        findRecyclerViewSafe(mealType)?.adapter?.notifyDataSetChanged()
                    }
                    refreshFoodData()
                } else {
                    refreshFoodData()
                }
                preloadDayData()
            }
        }
    }

    private suspend fun validateDayNavigation(targetDay: Calendar): Boolean {
        return if (targetDay.before(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) })) {
            (0..2).any {
                val checkDay = (targetDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, it + 1) }
                diaryDatabaseHelper.hasDataForDate(dateFormatter.format(checkDay.time))
            }
        } else true
    }

    private fun refreshFoodData() {
        mealData.values.forEach { it.clear() }
        mealData.keys.forEach { mealType ->
            findRecyclerViewSafe(mealType)?.adapter?.notifyDataSetChanged()
        }
        loadFoodDataFromDatabase()
    }

    private fun findRecyclerViewSafe(mealType: String): RecyclerView? {
        return when (mealType) {
            "breakfast" -> _binding?.breakfastFoodList
            "lunch" -> _binding?.lunchFoodList
            "dinner" -> _binding?.dinnerFoodList
            else -> null
        }
    }

    private fun loadFoodDataFromDatabase() {
        currentJob?.cancel()
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            val formattedDate = dateFormatter.format(currentDay.time)
            mealData.forEach { (mealType, foodList) ->
                val foodItems = diaryDatabaseHelper.getFoodItemsForMeal(formattedDate, mealType)
                withContext(Dispatchers.Main) {
                    foodList.addAll(foodItems.map { diaryDatabaseHelper.createFoodItem(it) })
                    findRecyclerViewSafe(mealType)?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun deleteFoodFragment(foodItem: FoodItem, mealType: String) {
        val formattedDate = dateFormatter.format(currentDay.time)

        // Delete from the database
        CoroutineScope(Dispatchers.IO).launch {
            val foodFragment = foodItem.toFoodFragmentEntity(formattedDate, mealType)

            Log.d("DiaryFragment", "BEFORE DELETION: " + diaryDatabaseHelper.getMealsForDate(formattedDate).size)

            diaryDatabaseHelper.deleteFoodFragmentFromDatabase(formattedDate, mealType, foodFragment)

            Log.d("DiaryFragment", "AFTER DELETION: " + diaryDatabaseHelper.getMealsForDate(formattedDate).size)
        }
    }

    private fun FoodItem.toFoodFragmentEntity(date: String, mealType: String): FoodFragment {
        return FoodFragment (
            image = this.image.toByteArray(),
            nutritionInfo = this.nutrition
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
        _binding = null
    }
}
