package com.intake.intakevisor

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.diary.FoodItem
import com.intake.intakevisor.diary.FoodItemAdapter

class DiaryActivity : AppCompatActivity() {

    // Initialize the mutable lists for each meal's food items
    private val breakfastItems = mutableListOf<FoodItem>()
    private val lunchItems = mutableListOf<FoodItem>()
    private val dinnerItems = mutableListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Initialize RecyclerViews
        val breakfastRecyclerView: RecyclerView = findViewById(R.id.breakfastFoodList)
        val lunchRecyclerView: RecyclerView = findViewById(R.id.lunchFoodList)
        val dinnerRecyclerView: RecyclerView = findViewById(R.id.dinnerFoodList)

        // Set LayoutManager
        breakfastRecyclerView.layoutManager = LinearLayoutManager(this)
        lunchRecyclerView.layoutManager = LinearLayoutManager(this)
        dinnerRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set Adapter (empty initially)
        breakfastRecyclerView.adapter = FoodItemAdapter(breakfastItems)
        lunchRecyclerView.adapter = FoodItemAdapter(lunchItems)
        dinnerRecyclerView.adapter = FoodItemAdapter(dinnerItems)

        // Set button listeners to open CameraActivity with the corresponding meal type
        findViewById<Button>(R.id.addBreakfast).setOnClickListener {
            openCameraActivity("breakfast")
        }
        findViewById<Button>(R.id.addLunch).setOnClickListener {
            openCameraActivity("lunch")
        }
        findViewById<Button>(R.id.addDinner).setOnClickListener {
            openCameraActivity("dinner")
        }

        // Handle passed data directly in onCreate since CameraActivity calls DiaryActivity
        handleFoodFragments()
    }

    private fun openCameraActivity(mealType: String) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("meal_type", mealType)
        startActivity(intent)
    }

    private fun handleFoodFragments() {
        val foodFragments: ArrayList<FoodFragment>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("food_fragments", FoodFragment::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("food_fragments")
        }

        val mealType = intent.getStringExtra("meal_type") ?: return

        Log.d("DiaryActivity", "$foodFragments")

        foodFragments?.forEach { foodFragment ->
            // Decode the bitmap from the byte array in the FoodFragment
            val bitmap = BitmapFactory.decodeByteArray(foodFragment.image, 0, foodFragment.image.size)

            // Get the nutrition information
            val nutritionInfo = foodFragment.nutritionInfo
            val name = nutritionInfo.name
            val calories = nutritionInfo.calories
            val nutrients = nutritionInfo.nutrients

            Log.d("DiaryActivity", "$name $calories $nutrients")

            // Create a new FoodItem object
            val foodItem = FoodItem("$name $calories $nutrients", bitmap)

            // Add food item to the appropriate list based on the meal type
            when (mealType) {
                "breakfast" -> {
                    breakfastItems.add(foodItem)
                    findViewById<RecyclerView>(R.id.breakfastFoodList).adapter?.notifyItemInserted(breakfastItems.size - 1)
                }
                "lunch" -> {
                    lunchItems.add(foodItem)
                    findViewById<RecyclerView>(R.id.lunchFoodList).adapter?.notifyItemInserted(lunchItems.size - 1)
                }
                "dinner" -> {
                    dinnerItems.add(foodItem)
                    findViewById<RecyclerView>(R.id.dinnerFoodList).adapter?.notifyItemInserted(dinnerItems.size - 1)
                }
            }
        }
    }
}
