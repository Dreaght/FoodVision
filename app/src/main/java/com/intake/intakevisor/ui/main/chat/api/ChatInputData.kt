package com.intake.intakevisor.ui.main.chat.api

import com.intake.intakevisor.ui.main.feedback.api.model.ReportPage
import com.intake.intakevisor.ui.welcome.UserData

data class ChatInputData(
    val message: String,
    val data: List<ReportPage>,
    val user: UserData
)
