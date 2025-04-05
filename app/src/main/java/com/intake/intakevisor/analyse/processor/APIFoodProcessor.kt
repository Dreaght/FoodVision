package com.intake.intakevisor.analyse.processor

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.intake.intakevisor.analyse.FoodRegion
import com.intake.intakevisor.analyse.Frame
import com.intake.intakevisor.analyse.processor.model.FoodFragmentAPI
import com.intake.intakevisor.api.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class APIFoodProcessor(frame: Frame) : FoodDetector {

    private val image = frame.image as Bitmap
    private val api = RetrofitClient.api

    override suspend fun detectFoods(): List<FoodRegion> {
        return withContext(Dispatchers.IO) {
            try {
                val imagePart = prepareImage(image)
                val response = api.uploadImage(imagePart)

                // Parse the JSON from response.message into a list of DetectedFoodRegion
                val detectedRegions: List<FoodFragmentAPI> = response.regions
                Log.d("APIFoodProcessor", "Detected regions: $detectedRegions")

                // Convert to FoodRegion while cropping actual image fragments
                detectedRegions.map { region ->
                    val croppedBitmap = Bitmap.createBitmap(
                        image,
                        region.start.X,
                        region.start.Y,
                        region.end.X - region.start.X,
                        region.end.Y - region.start.Y
                    )
                    FoodRegion(
                        Rect(region.start.X, region.start.Y, region.end.X, region.end.Y),
                        croppedBitmap,
                        region.nutrition
                    )
                }
            } catch (e: Exception) {
                Log.e("APIFoodProcessor", "Error processing image: ${e.message}")
                emptyList()
            }
        }
    }

    private fun prepareImage(bitmap: Bitmap): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream) // Use JPEG for efficiency
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        return MultipartBody.Part.createFormData("file", "food.jpg", requestBody)
    }
}
