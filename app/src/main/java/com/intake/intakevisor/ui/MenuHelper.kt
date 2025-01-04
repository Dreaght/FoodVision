package com.intake.intakevisor.ui

import android.app.Activity
import android.content.Intent
import com.intake.intakevisor.ChatActivity
import com.intake.intakevisor.DiaryActivity
import com.intake.intakevisor.ui.feedback.FeedbackActivity
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.ui.settings.SettingsActivity
import com.intake.intakevisor.R

class MenuHelper(private val activity: Activity) {

    fun setupMenu(binding: MenuPanelBinding) {
        if (activity is DiaryActivity) {
            activateDiary(binding)
        } else if (activity is FeedbackActivity) {
            activateFeedback(binding)
        } else if (activity is ChatActivity) {
            activateChat(binding)
        } else if (activity is SettingsActivity) {
            activateSettings(binding)
        }

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
        binding.btnDiary.setBackgroundResource(R.drawable.selected_activity_background)
        binding.btnFeedback.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnChat.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnSettings.setBackgroundResource(R.drawable.unselected_activity_background)
    }

    private fun activateFeedback(binding: MenuPanelBinding) {
        binding.btnDiary.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnFeedback.setBackgroundResource(R.drawable.selected_activity_background)
        binding.btnChat.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnSettings.setBackgroundResource(R.drawable.unselected_activity_background)
    }

    private fun activateChat(binding: MenuPanelBinding) {
        binding.btnDiary.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnFeedback.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnChat.setBackgroundResource(R.drawable.selected_activity_background)
        binding.btnSettings.setBackgroundResource(R.drawable.unselected_activity_background)
    }

    private fun activateSettings(binding: MenuPanelBinding) {
        binding.btnDiary.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnFeedback.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnChat.setBackgroundResource(R.drawable.unselected_activity_background)
        binding.btnSettings.setBackgroundResource(R.drawable.selected_activity_background)
    }
}
