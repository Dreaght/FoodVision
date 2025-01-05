package com.intake.intakevisor.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.content.res.AppCompatResources
import com.intake.intakevisor.ui.chat.ChatActivity
import com.intake.intakevisor.DiaryActivity
import com.intake.intakevisor.ui.feedback.FeedbackActivity
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.ui.settings.SettingsActivity
import com.intake.intakevisor.R

class MenuHelper(private val activity: Activity) {

    fun setupMenu(binding: MenuPanelBinding) {
        // Set active menu item based on current activity
        when (activity) {
            is DiaryActivity -> activateDiary(binding)
            is FeedbackActivity -> activateFeedback(binding)
            is ChatActivity -> activateChat(binding)
            is SettingsActivity -> activateSettings(binding)
        }

        // Set up click listeners
        binding.btnDiary.setOnClickListener {
            activity.startActivity(Intent(activity, DiaryActivity::class.java))
            activity.finish()
            activateDiary(binding)
        }
        binding.btnFeedback.setOnClickListener {
            activity.startActivity(Intent(activity, FeedbackActivity::class.java))
            activity.finish()
            activateFeedback(binding)
        }
        binding.btnChat.setOnClickListener {
            activity.startActivity(Intent(activity, ChatActivity::class.java))
            activity.finish()
            activateChat(binding)
        }
        binding.btnSettings.setOnClickListener {
            activity.startActivity(Intent(activity, SettingsActivity::class.java))
            activity.finish()
            activateSettings(binding)
        }
    }

    private fun activateDiary(binding: MenuPanelBinding) {
        // Set selected/unselected background for Diary button (ConstraintLayout)
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    private fun activateFeedback(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    private fun activateChat(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
    }

    private fun activateSettings(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.unselected_activity_background)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.selected_activity_background)
    }
}
