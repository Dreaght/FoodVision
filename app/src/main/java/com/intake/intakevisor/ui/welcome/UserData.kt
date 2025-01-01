package com.intake.intakevisor.ui.welcome

data class UserData(
    var gender: String = "Male",
    var weight: Int = 70,
    var height: Int = 170,
    var age: Int = 25,
    var goalWeight: Int = 70,
    var isOtherButtonSelected: Boolean = false
) {
    companion object {
        const val MIN_AGE = 1
        const val MIN_HEIGHT = 50 // cm
        const val MIN_WEIGHT = 10  // kg
    }
}
