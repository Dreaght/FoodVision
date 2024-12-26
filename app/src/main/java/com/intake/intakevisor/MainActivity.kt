package com.intake.intakevisor

import android.os.Bundle
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.intake.intakevisor.analyse.CameraController
import com.intake.intakevisor.analyse.FoodProcessor
import com.intake.intakevisor.analyse.FoodRegion
import com.intake.intakevisor.analyse.Frame
import com.intake.intakevisor.analyse.NutritionInfo

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreview: TextureView
    private lateinit var cameraController: CameraController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreview = findViewById(R.id.cameraPreview)
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
