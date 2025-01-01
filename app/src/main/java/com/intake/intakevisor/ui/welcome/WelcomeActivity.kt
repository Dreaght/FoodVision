package com.intake.intakevisor.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.intake.intakevisor.BaseActivity
import com.intake.intakevisor.DiaryActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.ActivityWelcomeBinding

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    var userData = UserData()

    // SharedPreferences setup
    private val sharedPreferences by lazy {
        getSharedPreferences("UserPreferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData() // Load user data on activity start

        loadFragment(WelcomePropertiesFragment())
    }

    // Method to switch fragments
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Enables back navigation
            .commit()
    }

    private fun saveUserData() {
        with(sharedPreferences.edit()) {
            putString("gender", userData.gender)
            putInt("weight", userData.weight)
            putInt("height", userData.height)
            putInt("age", userData.age)
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
    }

    fun finishWelcome() {
        saveUserData()

        val preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        preferences.edit().putBoolean("isFirstRun", false).apply()

        startActivity(Intent(this, DiaryActivity::class.java))
        finish()
    }
}
