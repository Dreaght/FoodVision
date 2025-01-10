package com.intake.intakevisor.ui.main

import android.os.Handler
import android.util.Log
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

    // Flag to prevent fragment switching during the delay
    private var isTransactionInProgress = false
    var feedbackDialogShown = false

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
        // Prevent fragment switching if a transaction is already in progress
        if (isTransactionInProgress) return

        val tag = fragment::class.java.name // Use the fragment's class name as a unique tag
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        val transaction = supportFragmentManager.beginTransaction()

        // Set the flag to true, indicating a transaction is in progress
        isTransactionInProgress = true

        // If the user is already on this fragment, reset it
        if (::currentFragment.isInitialized) {
            if (currentFragment.tag == tag) {
                // If it's the same fragment, remove and recreate it (reset state)
                transaction.remove(currentFragment)
                currentFragment = fragment // Create a new instance
                transaction.add(R.id.fragment_container, currentFragment, tag)
                Handler().postDelayed({
                    // Commit the transaction after delay
                    transaction.commitAllowingStateLoss()  // Allowing state loss to handle edge cases
                    activateItemInMenu(currentFragment)
                    // After transaction is completed, allow further switches
                    isTransactionInProgress = false
                }, 600)
                return
            } else {
                // If it's a different fragment, just hide the current one
                transaction.setCustomAnimations(
                    android.R.anim.fade_in, // Enter animation
                    android.R.anim.fade_out // Exit animation
                )
                transaction.hide(currentFragment) // Hide the current fragment
                previousFragment = currentFragment // Remember the previous fragment
            }
        }

        // Show the existing fragment if available, or create a new one
        if (existingFragment != null) {
            transaction.show(existingFragment)
            currentFragment = existingFragment
        } else {
            // Add the fragment if it's not found in fragment manager
            transaction.add(R.id.fragment_container, fragment, tag)
            currentFragment = fragment
        }

        Log.d("BaseMenuActivity", "Fragment shown: ${currentFragment::class.java.simpleName}")

        // Commit the transaction after a slight delay for smoother transitions
        Handler().postDelayed({
            transaction.commitAllowingStateLoss()  // Commit after delay
            activateItemInMenu(currentFragment)
            isTransactionInProgress = false
        }, 100)
    }

    fun activateItemInMenu(fragment: Fragment) {
        currentFragment = fragment

        if (currentFragment is FeedbackFragment && currentFragment.isAdded) {
            if (!(currentFragment as FeedbackFragment).isWeekSelected) {
                if (!feedbackDialogShown) {
                    feedbackDialogShown = true
                } else {
                    (currentFragment as FeedbackFragment).showDialog()
                }
            }
        }

        when (fragment) {
            is DiaryFragment -> menuHelper.activateDiary(binding)
            is FeedbackFragment -> menuHelper.activateFeedback(binding)
            is ChatFragment -> menuHelper.activateChat(binding)
            is SettingsFragment -> menuHelper.activateSettings(binding)
        }
        Log.d("BaseMenuActivity", "Activated item in menu: ${fragment::class.java.simpleName}")
    }
}
