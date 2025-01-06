package com.intake.intakevisor.ui.main

import android.os.Bundle
import com.intake.intakevisor.databinding.ActivityMainBinding
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment

class MainActivity : BaseMenuActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getStringExtra("fragment")) {
            "settings" -> loadFragment(SettingsFragment())
            else -> loadFragment(DiaryFragment())
        }
    }
}
