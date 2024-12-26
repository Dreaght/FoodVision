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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreview = findViewById(R.id.cameraPreview)
        startCameraPreview()
    }

    private fun startCameraPreview() {
        // Initialize and configure the camera here
        // Pass frames to `FoodProcessor` for analysis
        val foodProcessor = FoodProcessor()

        CameraController(cameraPreview) { currentFrame ->
            currentFrame?.let { frame ->
                val detectedFoods = foodProcessor.detectFoodRegions(frame)
                renderDetectedFoods(frame, detectedFoods, foodProcessor)
            }
        }.start()
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
}
