package com.intake.intakevisor.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.intake.intakevisor.ChatActivity
import com.intake.intakevisor.DiaryActivity
import com.intake.intakevisor.ui.feedback.FeedbackActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.ui.welcome.WelcomeActivity

class MenuHelper(private val activity: Activity) {

    fun setupMenu(view: View) {
        val diaryButton = view.findViewById<ImageView>(R.id.btn_diary)
        val feedbackButton = view.findViewById<ImageView>(R.id.btn_feedback)
        val chatButton = view.findViewById<ImageView>(R.id.btn_chat)
        val settingsButton = view.findViewById<ImageView>(R.id.btn_settings)

        diaryButton.setOnClickListener {
            activity.startActivity(Intent(activity, DiaryActivity::class.java))
        }
        feedbackButton.setOnClickListener {
            activity.startActivity(Intent(activity, FeedbackActivity::class.java))
        }
        chatButton.setOnClickListener {
            activity.startActivity(Intent(activity, ChatActivity::class.java))
        }
        settingsButton.setOnClickListener {
            activity.startActivity(Intent(activity, WelcomeActivity::class.java))
        }
    }
}
