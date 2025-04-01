package com.intake.intakevisor.ui.main.chat.api

import android.content.Context
import kotlinx.coroutines.delay
import kotlin.random.Random

class DummyBot : AssistantBot {

    private val responses = mutableListOf(
        "Hello! I understand you'd like some help," +
                " but I'm not sure how to assist you since it seems previous approaches haven't worked." +
                " I'm here to help in any way I can!",
        "Engaging in regular physical activity and being mindful of portion sizes could be a good starting point." +
                " Let me know if you'd like further suggestions!",
        "I’d recommend reconsidering your current dietary choices and habits." +
                " Perhaps a fresh perspective on your meals might help. Would you like some examples?",
    )

    // Simulate streaming of AI response with delays
    override suspend fun getResponseFragments(message: String, context: Context, onFragmentReceived: (String) -> Unit) {
        val finalResponse = responses.firstOrNull() ?: ("Of course!" +
                " If you have any more questions or need further assistance in the future," +
                " feel free to reach out. Wishing you the best on your journey to better health and well-being!")
        responses.removeFirstOrNull()

        val responseFragments = finalResponse.split(" ")  // Split by words (or sentences) for fragments

        delay(Random.Default.nextLong(1000, 1500))

        for (fragment in responseFragments) {
            onFragmentReceived(fragment)

            delay(Random.Default.nextLong(80, 500))
        }
    }
}