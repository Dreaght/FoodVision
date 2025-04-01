package com.intake.intakevisor.analyse

import android.os.Parcel
import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class NutritionInfo(
    val name: String = "Apple",
    val calories: Int = 0,
    val transFat: Int = 0,
    val saturatedFat: Int = 0,
    val totalFat: Int = 0,
    val protein: Int = 0,
    val sugar: Int = 0,
    val cholesterol: Int = 0,
    val sodium: Int = 0,
    val calcium: Int = 0,
    val iodine: Int = 0,
    val iron: Int = 0,
    val magnesium: Int = 0,
    val potassium: Int = 0,
    val zinc: Int = 0,
    val vitaminA: Int = 0,
    val vitaminC: Int = 0,
    val vitaminD: Int = 0,
    val vitaminE: Int = 0,
    val vitaminK: Int = 0,
    val vitaminB1: Int = 0,
    val vitaminB2: Int = 0,
    val vitaminB3: Int = 0,
    val vitaminB5: Int = 0,
    val vitaminB6: Int = 0,
    val vitaminB7: Int = 0,
    val vitaminB9: Int = 0,
    val vitaminB12: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(),
        parcel.readInt(), parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(calories)
        parcel.writeInt(transFat)
        parcel.writeInt(saturatedFat)
        parcel.writeInt(totalFat)
        parcel.writeInt(protein)
        parcel.writeInt(sugar)
        parcel.writeInt(cholesterol)
        parcel.writeInt(sodium)
        parcel.writeInt(calcium)
        parcel.writeInt(iodine)
        parcel.writeInt(iron)
        parcel.writeInt(magnesium)
        parcel.writeInt(potassium)
        parcel.writeInt(zinc)
        parcel.writeInt(vitaminA)
        parcel.writeInt(vitaminC)
        parcel.writeInt(vitaminD)
        parcel.writeInt(vitaminE)
        parcel.writeInt(vitaminK)
        parcel.writeInt(vitaminB1)
        parcel.writeInt(vitaminB2)
        parcel.writeInt(vitaminB3)
        parcel.writeInt(vitaminB5)
        parcel.writeInt(vitaminB6)
        parcel.writeInt(vitaminB7)
        parcel.writeInt(vitaminB9)
        parcel.writeInt(vitaminB12)
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
        return result
    }

    companion object CREATOR : Parcelable.Creator<NutritionInfo> {
        override fun createFromParcel(parcel: Parcel): NutritionInfo = NutritionInfo(parcel)
        override fun newArray(size: Int): Array<NutritionInfo?> = arrayOfNulls(size)
    }

    class NutritionInfoConverter {
        @TypeConverter
        fun fromNutritionInfo(nutrition: NutritionInfo): String {
            return Json.encodeToString(nutrition)
        }

        @TypeConverter
        fun toNutritionInfo(json: String): NutritionInfo {
            return Json.decodeFromString(json)
        }
    }
}
