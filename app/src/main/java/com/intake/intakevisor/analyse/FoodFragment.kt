package com.intake.intakevisor.analyse

import android.os.Parcel
import android.os.Parcelable

data class FoodFragment(
    val image: ByteArray,
    val nutritionInfo: NutritionInfo
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createByteArray() ?: ByteArray(0),
        parcel.readParcelable(NutritionInfo::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(image)
        parcel.writeParcelable(nutritionInfo, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FoodFragment> {
        override fun createFromParcel(parcel: Parcel): FoodFragment = FoodFragment(parcel)
        override fun newArray(size: Int): Array<FoodFragment?> = arrayOfNulls(size)
    }
}
