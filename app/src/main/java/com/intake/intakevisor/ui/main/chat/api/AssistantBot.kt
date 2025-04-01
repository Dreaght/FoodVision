package com.intake.intakevisor.ui.main.chat.api

interface AssistantBot {
    suspend fun getResponseFragments(message: String, onFragmentReceived: (String) -> Unit)
}