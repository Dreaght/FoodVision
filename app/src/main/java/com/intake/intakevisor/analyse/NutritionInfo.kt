package com.intake.intakevisor.analyse

import android.os.Parcel
import android.os.Parcelable
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
@JsonClass(generateAdapter = true)
data class NutritionInfo(
    val name: String = "Apple",
    val calories: Double = 0.0,
    val transFat: Double = 0.0,
    val saturatedFat: Double = 0.0,
    val totalFat: Double = 0.0,
    val protein: Double = 0.0,
    val sugar: Double = 0.0,
    val cholesterol: Double = 0.0,
    val sodium: Double = 0.0,
    val calcium: Double = 0.0,
    val iodine: Double = 0.0,
    val iron: Double = 0.0,
    val magnesium: Double = 0.0,
    val potassium: Double = 0.0,
    val zinc: Double = 0.0,
    val vitaminA: Double = 0.0,
    val vitaminC: Double = 0.0,
    val vitaminD: Double = 0.0,
    val vitaminE: Double = 0.0,
    val vitaminK: Double = 0.0,
    val vitaminB1: Double = 0.0,
    val vitaminB2: Double = 0.0,
    val vitaminB3: Double = 0.0,
    val vitaminB5: Double = 0.0,
    val vitaminB6: Double = 0.0,
    val vitaminB7: Double = 0.0,
    val vitaminB9: Double = 0.0,
    val vitaminB12: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(),
        parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(),
        parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(),
        parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(),
        parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(), parcel.readDouble(),
        parcel.readDouble(), parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(calories)
        parcel.writeDouble(transFat)
        parcel.writeDouble(saturatedFat)
        parcel.writeDouble(totalFat)
        parcel.writeDouble(protein)
        parcel.writeDouble(sugar)
        parcel.writeDouble(cholesterol)
        parcel.writeDouble(sodium)
        parcel.writeDouble(calcium)
        parcel.writeDouble(iodine)
        parcel.writeDouble(iron)
        parcel.writeDouble(magnesium)
        parcel.writeDouble(potassium)
        parcel.writeDouble(zinc)
        parcel.writeDouble(vitaminA)
        parcel.writeDouble(vitaminC)
        parcel.writeDouble(vitaminD)
        parcel.writeDouble(vitaminE)
        parcel.writeDouble(vitaminK)
        parcel.writeDouble(vitaminB1)
        parcel.writeDouble(vitaminB2)
        parcel.writeDouble(vitaminB3)
        parcel.writeDouble(vitaminB5)
        parcel.writeDouble(vitaminB6)
        parcel.writeDouble(vitaminB7)
        parcel.writeDouble(vitaminB9)
        parcel.writeDouble(vitaminB12)
    }

    override fun describeContents(): Int = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NutritionInfo

        if (calories != other.calories) return false
        if (transFat != other.transFat) return false
        if (saturatedFat != other.saturatedFat) return false
        if (totalFat != other.totalFat) return false
        if (protein != other.protein) return false
        if (sugar != other.sugar) return false
        if (cholesterol != other.cholesterol) return false
        if (sodium != other.sodium) return false
        if (calcium != other.calcium) return false
        if (iodine != other.iodine) return false
        if (iron != other.iron) return false
        if (magnesium != other.magnesium) return false
        if (potassium != other.potassium) return false
        if (zinc != other.zinc) return false
        if (vitaminA != other.vitaminA) return false
        if (vitaminC != other.vitaminC) return false
        if (vitaminD != other.vitaminD) return false
        if (vitaminE != other.vitaminE) return false
        if (vitaminK != other.vitaminK) return false
        if (vitaminB1 != other.vitaminB1) return false
        if (vitaminB2 != other.vitaminB2) return false
        if (vitaminB3 != other.vitaminB3) return false
        if (vitaminB5 != other.vitaminB5) return false
        if (vitaminB6 != other.vitaminB6) return false
        if (vitaminB7 != other.vitaminB7) return false
        if (vitaminB9 != other.vitaminB9) return false
        if (vitaminB12 != other.vitaminB12) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = calories
        result = 31 * result + transFat
        result = 31 * result + saturatedFat
        result = 31 * result + totalFat
        result = 31 * result + protein
        result = 31 * result + sugar
        result = 31 * result + cholesterol
        result = 31 * result + sodium
        result = 31 * result + calcium
        result = 31 * result + iodine
        result = 31 * result + iron
        result = 31 * result + magnesium
        result = 31 * result + potassium
        result = 31 * result + zinc
        result = 31 * result + vitaminA
        result = 31 * result + vitaminC
        result = 31 * result + vitaminD
        result = 31 * result + vitaminE
        result = 31 * result + vitaminK
        result = 31 * result + vitaminB1
        result = 31 * result + vitaminB2
        result = 31 * result + vitaminB3
        result = 31 * result + vitaminB5
        result = 31 * result + vitaminB6
        result = 31 * result + vitaminB7
        result = 31 * result + vitaminB9
        result = 31 * result + vitaminB12
        result = 31 * result + name.hashCode()
        return result.toInt()
    }

    companion object CREATOR : Parcelable.Creator<NutritionInfo> {
        override fun createFromParcel(parcel: Parcel): NutritionInfo = NutritionInfo(parcel)
        override fun newArray(size: Int): Array<NutritionInfo?> = arrayOfNulls(size)
    }

    class NutritionInfoConverter {
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
}
