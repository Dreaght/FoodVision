package com.intake.intakevisor.analyse

import android.graphics.Bitmap
import android.graphics.Rect

data class FoodRegion(val bounds: Rect, val fragment: Bitmap, val nutritionInfo: NutritionInfo)
