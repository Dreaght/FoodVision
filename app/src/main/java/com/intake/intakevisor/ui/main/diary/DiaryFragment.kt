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
import androidx.lifecycle.lifecycleScope
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
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity
    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    private val currentDay = Calendar.getInstance()

    private val mealTypes = listOf("breakfast", "lunch", "dinner")
    private val mealData = mealTypes.associateWith { mutableListOf<FoodItem>() }.toMutableMap()
    private val recyclerViews by lazy {
        mapOf(
            "breakfast" to binding.breakfastFoodList,
            "lunch" to binding.lunchFoodList,
            "dinner" to binding.dinnerFoodList
        )
    }

    private var currentJob: Job? = null
    private val cachedData = mutableMapOf<Calendar, Map<String, List<FoodItem>>>()

    private val cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val mealType = result.data?.getStringExtra("meal_type") ?: return@registerForActivityResult
            val foodFragments = result.data?.getParcelableArrayListExtra<FoodFragment>("food_fragments") ?: return@registerForActivityResult
            saveFoodFragments(mealType, foodFragments)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DiaryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = requireActivity() as? MainActivity
            ?: error("Activity must be MainActivity")
        mainActivity.activateItemInMenu(this)

        diaryDatabaseHelper = DiaryDatabaseHelper(requireContext())

        setupUI()
        loadFoodDataFromDatabase()
        changeDay(0)
    }

    private fun setupUI() {
        applyInsets(binding.daySwitcher)
        binding.previousDay.setOnClickListener { changeDay(-1) }
        binding.nextDay.setOnClickListener { changeDay(1) }

        setupRecyclerViews()
        setupAddMealButtons()
    }

    private fun applyInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val marginTop = maxOf(
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                insets.displayCutout?.safeInsetTop ?: 0,
                dpToPx(20)
            )
            v.updateLayoutParams<ConstraintLayout.LayoutParams> { topMargin = marginTop }
            insets
        }
    }

    private fun setupRecyclerViews() {
        mealTypes.forEach { mealType ->
            recyclerViews[mealType]?.apply {
                layoutManager = LinearLayoutManager(mainActivity)
                adapter = FoodItemAdapter(mealData[mealType] ?: mutableListOf()) { foodItem ->
                    deleteFoodFragment(foodItem, mealType)
                }.also {
                    val touchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(it))
                    touchHelper.attachToRecyclerView(this)
                }
            }
        }
    }

    private fun setupAddMealButtons() = binding.run {
        addBreakfast.setOnClickListener { addMeal("breakfast") }
        addLunch.setOnClickListener { addMeal("lunch") }
        addDinner.setOnClickListener { addMeal("dinner") }
    }

    private fun addMeal(mealType: String) {
        val intent = Intent(mainActivity, CameraActivity::class.java).apply {
            putExtra("meal_type", mealType)
            putExtra("selected_date", dateFormatter.format(currentDay.time))
        }
        cameraActivityLauncher.launch(intent)
    }

    private fun saveFoodFragments(mealType: String, foodFragments: List<FoodFragment>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val date = dateFormatter.format(currentDay.time)
            foodFragments.forEach {
                diaryDatabaseHelper.saveFoodFragmentToDatabase(date, mealType, it)
            }
            withContext(Dispatchers.Main) {
                refreshFoodData()
                changeDay(0)
            }
        }
    }

    private fun refreshFoodData() {
        mealData.values.forEach { it.clear() }
        mealTypes.forEach { recyclerViews[it]?.adapter?.notifyDataSetChanged() }
        loadFoodDataFromDatabase()
    }

    private fun loadFoodDataFromDatabase() {
        currentJob?.cancel()
        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            val date = dateFormatter.format(currentDay.time)
            mealTypes.forEach { mealType ->
                val items = diaryDatabaseHelper.getFoodItemsForMeal(date, mealType)
                withContext(Dispatchers.Main) {
                    mealData[mealType]?.addAll(items.map { diaryDatabaseHelper.createFoodItem(it) })
                    recyclerViews[mealType]?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun changeDay(offset: Int) {
        val targetDay = (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        if (targetDay.after(Calendar.getInstance())) return

        currentJob?.cancel()
        currentJob = lifecycleScope.launch {
            if (!validateDayNavigation(targetDay)) return@launch

            currentDay.time = targetDay.time
            updateCurrentDayLabel()

            cachedData[targetDay]?.let { meals ->
                mealTypes.forEach { mealType ->
                    mealData[mealType]?.apply {
                        clear()
                        addAll(meals[mealType] ?: emptyList())
                    }
                    recyclerViews[mealType]?.adapter?.notifyDataSetChanged()
                }
                refreshFoodData()
            } ?: refreshFoodData()

            preloadAdjacentDays()
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

    private fun preloadAdjacentDays() {
        val adjacent = listOf(-1, 1).map { offset ->
            (currentDay.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            adjacent.forEach { day ->
                val meals = diaryDatabaseHelper.getMealsForDate(dateFormatter.format(day.time))
                cachedData[day] = meals
            }
        }
    }

    private fun updateCurrentDayLabel() {
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        binding.currentDay.text = when {
            isSameDay(currentDay, today) -> getString(R.string.today)
            isSameDay(currentDay, yesterday) -> getString(R.string.yesterday)
            else -> dateFormatter.format(currentDay.time)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar) =
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

    private fun deleteFoodFragment(foodItem: FoodItem, mealType: String) {
        val date = dateFormatter.format(currentDay.time)
        lifecycleScope.launch(Dispatchers.IO) {
            val entity = foodItem.toFoodFragmentEntity(date, mealType)
            diaryDatabaseHelper.deleteFoodFragmentFromDatabase(date, mealType, entity)
        }
    }

    private fun FoodItem.toFoodFragmentEntity(date: String, mealType: String) = FoodFragment(
        image = this.image.toByteArray(),
        nutritionInfo = this.nutrition
    )

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
        _binding = null
    }
}

