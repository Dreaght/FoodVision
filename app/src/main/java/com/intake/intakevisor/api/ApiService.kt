package com.intake.intakevisor.api

import com.intake.intakevisor.api.request.ChatRequest
import com.intake.intakevisor.api.request.ReportRequest
import com.intake.intakevisor.api.request.UploadResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("upload/")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): UploadResponse

    @POST("report/")
    suspend fun sendReport(
        @Body request: ReportRequest
    ): Response<ResponseBody>

    @Streaming
    @POST("chat/")
    suspend fun sendMessage(@Body request: String): ResponseBody
}
