package com.intake.intakevisor.ui.main

import androidx.appcompat.content.res.AppCompatResources
import com.intake.intakevisor.databinding.MenuPanelBinding
import com.intake.intakevisor.R
import com.intake.intakevisor.ui.main.chat.ChatFragment
import com.intake.intakevisor.ui.main.diary.DiaryFragment
import com.intake.intakevisor.ui.main.feedback.FeedbackFragment
import com.intake.intakevisor.ui.main.settings.SettingsFragment

class MenuHelper(private val activity: MainActivity) {

    fun setupMenu(binding: MenuPanelBinding) {
        // Set up click listeners
        binding.btnDiary.setOnClickListener {
            activity.loadFragment(DiaryFragment())
        }
        binding.btnFeedback.setOnClickListener {
            activity.loadFragment(FeedbackFragment())
        }
        binding.btnChat.setOnClickListener {
            activity.loadFragment(ChatFragment())
        }
        binding.btnSettings.setOnClickListener {
            activity.loadFragment(SettingsFragment())
        }
    }

    fun activateDiary(binding: MenuPanelBinding) {
        // Set selected/unselected background for Diary button (ConstraintLayout)
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.gray_rounded_square)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
    }

    fun activateFeedback(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.gray_rounded_square)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
    }

    fun activateChat(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.gray_rounded_square)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
    }

    fun activateSettings(binding: MenuPanelBinding) {
        binding.btnDiary.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnFeedback.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnChat.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.white_rounded_square)
        binding.btnSettings.background = AppCompatResources.getDrawable(activity.applicationContext, R.drawable.gray_rounded_square)
    }
}
