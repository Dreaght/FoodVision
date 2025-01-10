package com.intake.intakevisor.ui.main

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.intake.intakevisor.databinding.ActivityMainBinding
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment

class MainActivity : BaseMenuActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
