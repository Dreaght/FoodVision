package com.intake.intakevisor.analyse

import android.graphics.Bitmap
import android.graphics.Rect
import kotlin.random.Random

class FoodProcessor(val frame: Frame) : FoodDetector {

    private val image = frame.image as Bitmap

    override fun detectFoods(): List<FoodRegion> {
        // Get the frame dimensions
        val frameWidth = image.width
        val frameHeight = image.height

        // Generate 3 random regions
        val regions = List(3) {
            createRandomRegion(frameWidth, frameHeight)
        }

        // Extract fragments from the frame based on the regions
        val foodRegions = regions.map { rect ->
            val fragment = cropBitmap(image, rect)
            FoodRegion(rect, fragment, NutritionInfo("Sample Food", 100))
        }

        return foodRegions
    }

    private fun createRandomRegion(frameWidth: Int, frameHeight: Int): Rect {
        val maxWidth = (frameWidth * 0.3).toInt()
        val maxHeight = (frameHeight * 0.3).toInt()

        // Generate random top-left corner
        val left = Random.nextInt(0, frameWidth - maxWidth)
        val top = Random.nextInt(0, frameHeight - maxHeight)

        // Ensure width and height are within bounds
        val width = Random.nextInt(50, maxWidth.coerceAtLeast(50)) // At least 50 pixels
        val height = Random.nextInt(50, maxHeight.coerceAtLeast(50)) // At least 50 pixels

        return Rect(left, top, left + width, top + height)
    }

    private fun cropBitmap(source: Bitmap, rect: Rect): Bitmap {
        val left = rect.left.coerceAtLeast(0)
        val top = rect.top.coerceAtLeast(0)
        val width = rect.width().coerceAtMost(source.width - left)
        val height = rect.height().coerceAtMost(source.height - top)

        // Ensure width and height are positive
        if (width <= 0 || height <= 0) {
            throw IllegalArgumentException("Invalid region dimensions: width=$width, height=$height")
        }

        return Bitmap.createBitmap(
            source,
            left,
            top,
            width,
            height
        )
    }
}
