package com.intake.intakevisor.util

import android.graphics.Bitmap

class BitmapUtil {
    companion object {
        fun resizeBitmapToFitBounds(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height

            val widthScale = maxWidth.toFloat() / width
            val heightScale = maxHeight.toFloat() / height
            val scale = minOf(widthScale, heightScale)

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
    }
}