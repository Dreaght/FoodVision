package com.intake.intakevisor

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.TextureView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.intake.intakevisor.analyse.*
import com.intake.intakevisor.analyse.camera.CameraController

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreview: TextureView
    private lateinit var cameraController: CameraController
    private lateinit var galleryPreview: ImageView // ImageView to show the last photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreview = findViewById(R.id.cameraPreview)
        galleryPreview = findViewById(R.id.gallery_preview) // Find the ImageView

        // Load the last photo from the gallery
        loadLastPhotoIntoPreview()

        startCameraPreview()
    }

    private fun startCameraPreview() {
        // Initialize and configure the camera here
        val foodProcessor = FoodProcessor()

        // Initialize the CameraController
        cameraController = CameraController(cameraPreview) { currentFrame ->
            currentFrame?.let { frame ->
                val detectedFoods = foodProcessor.detectFoodRegions(frame)
                renderDetectedFoods(frame, detectedFoods, foodProcessor)
            }
        }

        // Start the camera preview
        cameraController.start()
    }

    private fun loadLastPhotoIntoPreview() {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(collection, projection, null, null, sortOrder)

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = it.getLong(idColumn)

                // Create URI for the last image
                val lastPhotoUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                // Load the image into the gallery preview
                Glide.with(this)
                    .load(lastPhotoUri)
                    .placeholder(R.drawable.transparent_square) // Transparent background if no image
                    .into(galleryPreview)
            } else {
                // No photos available; load a transparent placeholder
                galleryPreview.setImageResource(R.drawable.transparent_square)
            }
        }
    }

    private fun renderDetectedFoods(
        frame: Frame,
        detectedFoods: List<FoodRegion>,
        foodProcessor: FoodProcessor
    ) {
        detectedFoods.forEach { region ->
            val nutritionInfo = foodProcessor.analyze(region)
            drawBoundingBox(region)
            drawNutritionInfo(region, nutritionInfo)
        }
    }

    private fun drawBoundingBox(region: FoodRegion) {
        // Use OpenCV or another method to draw a rectangle on the preview
    }

    private fun drawNutritionInfo(region: FoodRegion, info: NutritionInfo) {
        // Draw text near the bounding box, ensure it's within screen bounds
    }

    override fun onResume() {
        super.onResume()
        cameraController.resume() // Resume camera when the app comes back to the foreground
    }

    override fun onPause() {
        super.onPause()
        cameraController.pause() // Pause camera when the app goes into the background
    }
}
