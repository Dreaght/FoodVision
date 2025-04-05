package com.intake.intakevisor.ui.main.diary

import android.graphics.Bitmap

import android.os.Parcel
import android.os.Parcelable
import com.intake.intakevisor.analyse.NutritionInfo
import java.io.ByteArrayOutputStream

data class FoodItem(val nutrition: NutritionInfo, val image: Bitmap) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable<NutritionInfo>(NutritionInfo::class.java.classLoader)!!,
        parcel.readParcelable<Bitmap>(Bitmap::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(nutrition, flags)
        parcel.writeParcelable(image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodItem> {
        override fun createFromParcel(parcel: Parcel): FoodItem {
            return FoodItem(parcel)
        }

        override fun newArray(size: Int): Array<FoodItem?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FoodItem
        return nutrition == other.nutrition && image.sameAs(other.image)
    }

    override fun hashCode(): Int {
        var result = nutrition.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}

internal fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
