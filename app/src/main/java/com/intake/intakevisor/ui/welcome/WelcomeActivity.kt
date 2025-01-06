package com.intake.intakevisor.ui.welcome

import android.content.Intent
import android.os.Bundle
import com.intake.intakevisor.BaseActivity
import com.intake.intakevisor.databinding.ActivityWelcomeBinding
import com.intake.intakevisor.ui.main.MainActivity

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    var userData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData() // Load user data on activity start

        loadFragment(WelcomePropertiesFragment())
    }

    private fun saveUserData() {
        with(sharedPreferences.edit()) {
            putString("gender", userData.gender)
            putInt("weight", userData.weight)
            putInt("height", userData.height)
            putInt("age", userData.age)
            putString("birthDate", userData.birthDate)
            putInt("goalWeight", userData.goalWeight)
            apply() // Apply changes asynchronously
        }
    }

    private fun loadUserData() {
        userData.gender = sharedPreferences.getString("gender", userData.gender) ?: userData.gender
        userData.weight = sharedPreferences.getInt("weight", userData.weight)
        userData.height = sharedPreferences.getInt("height", userData.height)
        userData.age = sharedPreferences.getInt("age", userData.age)
        userData.goalWeight = sharedPreferences.getInt("goalWeight", userData.goalWeight)
        userData.birthDate = sharedPreferences.getString("birthDate", userData.birthDate)!!
    }

    fun finishWelcome() {
        saveUserData()

        val preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        preferences.edit().putBoolean("isFirstRun", false).apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
