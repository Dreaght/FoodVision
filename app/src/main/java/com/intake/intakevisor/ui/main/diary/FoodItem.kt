package com.intake.intakevisor.ui.main.diary

import android.graphics.Bitmap

import android.os.Parcel
import android.os.Parcelable

data class FoodItem(val name: String, val image: Bitmap) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readParcelable<Bitmap>(Bitmap::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
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
        return name == other.name && image.sameAs(other.image)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + image.hashCode()
        return result
    }
}
