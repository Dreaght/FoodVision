package com.intake.intakevisor.ui.main

import android.app.Activity
import androidx.appcompat.content.res.AppCompatResources
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.R
import com.intake.intakevisor.ui.main.chat.ChatFragment
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.feedback.FeedbackFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment

class MenuHelper(private val activity: Activity) {

    fun setupMenu(binding: MenuPanelBinding) {
        // Set up click listeners
        binding.btnDiary.setOnClickListener {
            (activity as MainActivity).loadFragment(DiaryFragment())
        }
        binding.btnFeedback.setOnClickListener {
            (activity as MainActivity).loadFragment(FeedbackFragment())
        }
        binding.btnChat.setOnClickListener {
            (activity as MainActivity).loadFragment(ChatFragment())
        }
        binding.btnSettings.setOnClickListener {
            (activity as MainActivity).loadFragment(SettingsFragment())
        }
    }

    fun activateDiary(binding: MenuPanelBinding) {
        // Set selected/unselected background for Diary button (ConstraintLayout)
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    fun activateFeedback(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    fun activateChat(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    fun activateSettings(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
    }
}
