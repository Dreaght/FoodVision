package com.intake.intakevisor.ui.chat


data class Message(val sender: String, val text: String) {
    companion object {
        const val SENDER_USER = "user"
        const val SENDER_ASSISTANT = "assistant"
    }
}
