package com.intake.intakevisor.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_fragments")
data class FoodFragmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // auto-generate the ID
    val dateMealTypeKey: String,
    val date: String,
    val mealType: String,
    val image: ByteArray,
    val name: String,
    val calories: Int
)
