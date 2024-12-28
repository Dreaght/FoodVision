package com.intake.intakevisor

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.intake.intakevisor.analyse.FoodFragment

class DiaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val foodFragments: ArrayList<FoodFragment>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("food_fragments", FoodFragment::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("food_fragments")
        }

        foodFragments?.forEach { foodFragment ->
            val bitmap = BitmapFactory.decodeByteArray(foodFragment.image, 0, foodFragment.image.size)

            val nutritionInfo = foodFragment.nutritionInfo
            val name = nutritionInfo.name
            val calories = nutritionInfo.calories
            val nutrients = nutritionInfo.nutrients

            Log.d("DiaryActivity", "$name $calories $nutrients")
        }
    }
}