package com.intake.intakevisor.ui.main

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.intake.intakevisor.BaseActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.ui.main.chat.ChatFragment
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.feedback.FeedbackFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment
import kotlinx.coroutines.*

open class BaseMenuActivity : BaseActivity() {
    private lateinit var binding: MenuPanelBinding
    private lateinit var menuHelper: MenuHelper

    var previousFragment: Fragment? = null
    lateinit var currentFragment: Fragment

    private val fragmentScope = CoroutineScope(Dispatchers.Main + SupervisorJob()) // CoroutineScope for fragment management
    private var fragmentJob: Job? = null // Job to handle fragment transactions

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
        val tag = fragment::class.java.name
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        // Cancel any ongoing fragment transaction job
        fragmentJob?.cancel()

        // Start a new fragment transaction job
        fragmentJob = fragmentScope.launch {
            val transaction = supportFragmentManager.beginTransaction()

            // Handle the current fragment
            if (::currentFragment.isInitialized) {
                if (currentFragment.tag == tag) {
                    // If it's the same fragment, reset its state
                    transaction.remove(currentFragment)
                    currentFragment = fragment
                    transaction.add(R.id.fragment_container, currentFragment, tag)
                    transaction.commitAllowingStateLoss()
                    activateItemInMenu(currentFragment)
                    removeDuplicateFragments() // Clean up after switch
                    return@launch
                } else {
                    // Set previousFragment before switching
                    previousFragment = currentFragment

                    // Fade out the current fragment
                    val currentFragmentView = currentFragment.view
                    if (currentFragmentView != null) {
                        currentFragmentView.animate()
                            .alpha(0f) // Fade out
                            .setDuration(300) // Animation duration
                            .withEndAction {
                                transaction.hide(currentFragment) // Hide the current fragment
                                performAddOrShowFragment(transaction, fragment, tag, existingFragment)
                                removeDuplicateFragments() // Clean up after switch
                            }.start()
                        delay(300) // Wait for the fade-out animation
                    } else {
                        // If no view exists, directly switch fragments
                        performAddOrShowFragment(transaction, fragment, tag, existingFragment)
                        removeDuplicateFragments() // Clean up after switch
                    }
                }
            } else {
                // No current fragment exists, directly add the new fragment
                currentFragment = fragment
                transaction.add(R.id.fragment_container, currentFragment, tag)

                transaction.commitAllowingStateLoss()
                activateItemInMenu(currentFragment)
                removeDuplicateFragments() // Clean up after switch
            }
        }
    }

    private fun removeDuplicateFragments() {
        val fragmentManager = supportFragmentManager
        val fragmentTags = mutableSetOf<String>()
        val fragmentsToRemove = mutableListOf<Fragment>()

        for (fragment in fragmentManager.fragments) {
            if (fragment.isAdded) {
                val tag = fragment::class.java.name
                if (fragmentTags.contains(tag) && fragment != currentFragment) {
                    // If fragment type already exists and it's not the current fragment, mark it for removal
                    fragmentsToRemove.add(fragment)
                } else {
                    fragmentTags.add(tag)
                }
            }
        }

        // Remove the duplicate fragments
        val transaction = fragmentManager.beginTransaction()
        for (fragment in fragmentsToRemove) {
            transaction.remove(fragment)
        }
        transaction.commitAllowingStateLoss()

        Log.d("BaseMenuActivity", "Removed duplicate fragments: ${fragmentsToRemove.size}")
    }

    private fun performAddOrShowFragment(
        transaction: FragmentTransaction,
        fragment: Fragment,
        tag: String,
        existingFragment: Fragment?
    ) {
        if (existingFragment != null) {
            // Use the existing fragment
            currentFragment = existingFragment
            val fragmentView = currentFragment.view
            if (fragmentView != null) {
                fragmentView.alpha = 0f // Start at alpha 0
                fragmentView.animate()
                    .alpha(1f) // Fade in
                    .setDuration(300) // Animation duration
                    .start()
            }
            transaction.show(existingFragment)
        } else {
            // Add the new fragment
            currentFragment = fragment
            transaction.add(R.id.fragment_container, fragment, tag)

            // Defer the alpha animation until the view is created
            supportFragmentManager.executePendingTransactions()
            val fragmentView = fragment.view
            if (fragmentView != null) {
                fragmentView.alpha = 0f // Start at alpha 0
                fragmentView.post {
                    fragmentView.animate()
                        .alpha(1f) // Fade in
                        .setDuration(300) // Animation duration
                        .start()
                }
            }
        }

        // Commit the transaction
        transaction.commitAllowingStateLoss()
        activateItemInMenu(currentFragment)
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

    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancel() // Cancel all coroutines when activity is destroyed
    }
}
