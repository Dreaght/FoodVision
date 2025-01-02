package com.intake.intakevisor.ui.feedback

import android.os.Bundle
import com.intake.intakevisor.BaseMenuActivity
import com.intake.intakevisor.databinding.ActivityFeedbackBinding

class FeedbackActivity : BaseMenuActivity() {
    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}