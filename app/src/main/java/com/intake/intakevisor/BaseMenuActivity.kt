package com.intake.intakevisor

import android.view.View
import com.intake.intakevisor.ui.MenuHelper

open class BaseMenuActivity : BaseActivity() {

    override fun onContentChanged() {
        super.onContentChanged()

        // Automatically initialize the menu for all activities that extend BaseActivity
        val menuPanel = findViewById<View>(R.id.menuPanel)
        if (menuPanel != null) {
            MenuHelper(this).setupMenu(menuPanel)
        }
    }
}