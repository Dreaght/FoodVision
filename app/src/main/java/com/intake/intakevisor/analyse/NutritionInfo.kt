package com.intake.intakevisor.analyse

import android.os.Parcel
import android.os.Parcelable

data class NutritionInfo(
    val name: String,
    val calories: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(calories)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NutritionInfo> {
        override fun createFromParcel(parcel: Parcel): NutritionInfo = NutritionInfo(parcel)
        override fun newArray(size: Int): Array<NutritionInfo?> = arrayOfNulls(size)
    }
}
