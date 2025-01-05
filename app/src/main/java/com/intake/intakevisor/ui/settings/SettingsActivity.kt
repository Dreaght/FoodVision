package com.intake.intakevisor.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.intake.intakevisor.BaseMenuActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.ActivitySettingsBinding
import com.intake.intakevisor.ui.welcome.UserData

class SettingsActivity : BaseMenuActivity() {
    private lateinit var binding: ActivitySettingsBinding

    var userData = UserData()

    private val sharedPreferences by lazy {
        getSharedPreferences("UserPreferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupUI()
    }

    private fun setupUI() {
        setupDatePicker()

        binding.weightSeekBar.progress = userData.weight
        binding.weightTitle.text = getString(R.string.weightTitle, userData.weight)
        binding.heightSeekBar.progress = userData.height
        binding.heightTitle.text = getString(R.string.heightTitle, userData.height)
        binding.weightGoalSeekBar.progress = userData.goalWeight
        binding.weightGoalTitle.text = getString(R.string.settings_weight_goal_title, userData.goalWeight)

        binding.confirmSettingsBtn.setOnClickListener {
            userData.weight = binding.weightSeekBar.progress
            userData.height = binding.heightSeekBar.progress
            userData.goalWeight = binding.weightGoalSeekBar.progress
            userData.birthDate = binding.birthDatePicker.year.toString() + "-" +
                    (binding.birthDatePicker.month + 1).toString() + "-" +
                    binding.birthDatePicker.dayOfMonth.toString()

            saveUserData()
            binding.confirmSettingsBtn.visibility = View.GONE
        }

        binding.weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum weight (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightTitle.text = getString(R.string.weightTitle, validProgress)
                userData.weight = validProgress
                binding.confirmSettingsBtn.visibility = View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Height TextView
        binding.heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum height (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_HEIGHT) UserData.MIN_HEIGHT else progress
                binding.heightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.heightTitle.text = getString(R.string.heightTitle, validProgress)
                userData.height = validProgress
                binding.confirmSettingsBtn.visibility = View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.weightGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightGoalSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightGoalTitle.text = getString(R.string.weightGoalTitle, validProgress)
                userData.goalWeight = validProgress
                binding.confirmSettingsBtn.visibility = View.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupDatePicker() {
        val birthDate = sharedPreferences.getString("birthDate", "2000-01-01")
        val (year, month, day) = birthDate!!.split("-").map { it.toInt() }

        binding.birthDatePicker.updateDate(year, month - 1, day) // Month is 0-indexed
        binding.birthDatePicker.setOnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            userData.birthDate = selectedDate
            binding.confirmSettingsBtn.visibility = View.VISIBLE
        }
    }

    private fun saveUserData() {
        with(sharedPreferences.edit()) {
            putInt("weight", userData.weight)
            putInt("height", userData.height)
            putInt("age", userData.age)
            putString("birthDate", userData.birthDate)
            putInt("goalWeight", userData.goalWeight)
            apply() // Apply changes asynchronously
        }
    }

    private fun loadUserData() {
        userData.weight = sharedPreferences.getInt("weight", userData.weight)
        userData.height = sharedPreferences.getInt("height", userData.height)
        userData.age = sharedPreferences.getInt("age", userData.age)
        userData.goalWeight = sharedPreferences.getInt("goalWeight", userData.goalWeight)
        userData.birthDate = sharedPreferences.getString("birthDate", userData.birthDate)!!
    }
}