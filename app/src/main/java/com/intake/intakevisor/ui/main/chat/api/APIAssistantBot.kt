package com.intake.intakevisor.ui.main.chat.api

import com.intake.intakevisor.api.RetrofitClient
import com.intake.intakevisor.api.request.ChatRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader

class APIAssistantBot : AssistantBot {

    private val api = RetrofitClient.api

    override suspend fun getResponseFragments(
        message: String,
        onFragmentReceived: (String) -> Unit
    ) {
        val responseBody: ResponseBody = api.sendMessage(ChatRequest(message))

        flow {
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                emit(line!!)
            }
        }
        .flowOn(Dispatchers.IO) // Process on IO thread
        .collect { fragment ->
            onFragmentReceived(fragment)
        }
    }
}
