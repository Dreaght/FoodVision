package com.intake.intakevisor.ui.main.chat.api

import android.content.Context

interface AssistantBot {
    suspend fun getResponseFragments(message: String, context: Context, onFragmentReceived: (String) -> Unit)
}