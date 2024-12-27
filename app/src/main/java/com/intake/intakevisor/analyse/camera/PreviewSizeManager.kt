package com.intake.intakevisor.analyse.camera

import android.util.Size
import kotlin.math.abs

class PreviewSizeManager {

    fun getBestPreviewSize(sizes: Array<Size>?, width: Int, height: Int): Size? {
        val targetRatio = width.toFloat() / height
        return sizes?.minByOrNull { size ->
            val ratio = size.width.toFloat() / size.height
            abs(ratio - targetRatio)
        }
    }
}