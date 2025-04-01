package com.intake.intakevisor.api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intake.intakevisor.api.request.ChatRequest
import com.intake.intakevisor.api.request.ReportRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyViewModel : ViewModel() {

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            try {
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
                val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

                val response = RetrofitClient.api.uploadImage(imagePart)
                Log.d("Upload", "Response: ${response.message}")
            } catch (e: Exception) {
                Log.e("Upload", "Error: ${e.message}")
            }
        }
    }

    fun sendReport(reportData: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.sendReport(ReportRequest(reportData))
                Log.d("Report", "Response: ${response.message()}")
            } catch (e: Exception) {
                Log.e("Report", "Error: ${e.message}")
            }
        }
    }

    fun sendMessage(chatMessage: String) {
        viewModelScope.launch {
            try {
//                val response = RetrofitClient.api.sendMessage(ChatRequest(chatMessage))
//                Log.d("Chat", "Reply: ${response.reply}")
            } catch (e: Exception) {
                Log.e("Chat", "Error: ${e.message}")
            }
        }
    }
}
