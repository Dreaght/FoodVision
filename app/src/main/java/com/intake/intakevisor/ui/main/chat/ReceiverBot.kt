package com.intake.intakevisor.ui.main.chat

import kotlinx.coroutines.*
import kotlin.random.Random

class ReceiverBot {

    private val responses = mutableListOf(
        "I don't know how to help you, fat asshole. Judging by your food, what are you eating, you're a fat ass.",
        "And? Stop devouring, get involved in sports, stop whining to me here. Nothing's going to help you swine."
    )

    // Simulate streaming of AI response with delays
    suspend fun getResponseFragments(message: String, onFragmentReceived: (String) -> Unit) {
        val finalResponse = responses.firstOrNull() ?: "I'm sorry, I don't understand."
        responses.removeFirstOrNull()

        val responseFragments = finalResponse.split(" ")  // Split by words (or sentences) for fragments

        delay(Random.nextLong(500, 1000))

        for (fragment in responseFragments) {
            onFragmentReceived(fragment)

            delay(Random.nextLong(50, 500))
        }
    }
}
