package com.intake.intakevisor

import android.view.View
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.ui.MenuHelper

open class BaseMenuActivity : BaseActivity() {

    private lateinit var binding: MenuPanelBinding

    override fun onContentChanged() {
        super.onContentChanged()

        // Bind the existing layout instead of inflating a new one
        val menuPanel = findViewById<View>(R.id.menuPanel)
        if (menuPanel != null) {
            binding = MenuPanelBinding.bind(menuPanel)
            MenuHelper(this).setupMenu(binding)
        }
    }
}
