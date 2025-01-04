package com.intake.intakevisor

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0) // Disable animation
        } else {
            overridePendingTransition(0, 0) // Fallback for older versions
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun finish() {
        super.finish()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0) // Disable animation
        } else {
            overridePendingTransition(0, 0) // Fallback for older versions
        }
    }

}
