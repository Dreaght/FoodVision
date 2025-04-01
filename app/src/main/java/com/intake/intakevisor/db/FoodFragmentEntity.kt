package com.intake.intakevisor.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.intake.intakevisor.analyse.NutritionInfo
import com.intake.intakevisor.analyse.NutritionInfo.NutritionInfoConverter

@Entity(tableName = "food_fragments")
data class FoodFragmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // auto-generate the ID
    val dateMealTypeKey: String,
    val date: String,
    val mealType: String,
    val image: ByteArray,

    @TypeConverters(NutritionInfoConverter::class) val nutrition: NutritionInfo
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodFragmentEntity

        if (id != other.id) return false
        if (dateMealTypeKey != other.dateMealTypeKey) return false
        if (date != other.date) return false
        if (mealType != other.mealType) return false
        if (!image.contentEquals(other.image)) return false
        if (nutrition != other.nutrition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateMealTypeKey.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + mealType.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + nutrition.hashCode()
        return result
    }

}
