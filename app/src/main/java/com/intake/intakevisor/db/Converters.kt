package com.intake.intakevisor.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intake.intakevisor.analyse.NutritionInfo

class Converters {
    @TypeConverter
    fun fromNutritionInfo(nutritionInfo: NutritionInfo): String {
        return Gson().toJson(nutritionInfo)
    }

    @TypeConverter
    fun toNutritionInfo(nutritionInfoString: String): NutritionInfo {
        val type = object : TypeToken<NutritionInfo>() {}.type
        return Gson().fromJson(nutritionInfoString, type)
    }
}
