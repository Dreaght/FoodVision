package com.intake.intakevisor.ui.main

import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.intake.intakevisor.BaseActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.ui.main.chat.ChatFragment
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.feedback.FeedbackFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment

open class BaseMenuActivity : BaseActivity() {

    private lateinit var binding: MenuPanelBinding
    private lateinit var menuHelper: MenuHelper

    lateinit var previousFragment: Fragment
    lateinit var currentFragment: Fragment

    override fun onContentChanged() {
        super.onContentChanged()

        // Bind the existing layout instead of inflating a new one
        val menuPanel = findViewById<View>(R.id.menuPanel)
        if (menuPanel != null) {
            binding = MenuPanelBinding.bind(menuPanel)
            menuHelper = MenuHelper(this as MainActivity)
            menuHelper.setupMenu(binding)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            menuHelper.setupMenu(binding)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun loadFragment(fragment: Fragment) {
        if (::currentFragment.isInitialized && fragment::class == currentFragment::class) {
            // If the new fragment is of the same type as the current one, return early to prevent duplication
            return
        }

        val transaction = supportFragmentManager.beginTransaction()

        if (::currentFragment.isInitialized) {
            // Hide the current fragment instead of removing it
            transaction.hide(currentFragment)
            previousFragment = currentFragment
        }

        if (!fragment.isAdded) {
            // Add the new fragment if it's not already added
            transaction.add(R.id.fragment_container, fragment)
        } else {
            // Show the existing fragment
            transaction.show(fragment)
        }

        transaction.commit()

        currentFragment = fragment
        activateItemInMenu(fragment)
    }

    fun activateItemInMenu(fragment: Fragment) {
        currentFragment = fragment
        when (fragment) {
            is DiaryFragment -> menuHelper.activateDiary(binding)
            is FeedbackFragment -> menuHelper.activateFeedback(binding)
            is ChatFragment -> menuHelper.activateChat(binding)
            is SettingsFragment -> menuHelper.activateSettings(binding)
        }
    }
}
