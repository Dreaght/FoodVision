package com.intake.intakevisor.analyse.widget

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.intake.intakevisor.analyse.FoodRegion

class TransparentOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var overlayBitmap: Bitmap? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f // Default stroke width
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = 0xFCFCFCFC.toInt() // Black color for text
        textSize = 36f // Set a readable text size
        isAntiAlias = true
    }

    private val regionsToDraw = mutableListOf<Pair<FoodRegion, Boolean>>() // FoodRegion and isSelected
    private val rect = RectF() // Preallocated RectF object to reuse

    fun clearBitMap() {
        overlayBitmap = null
        invalidate() // Request a redraw
    }

    fun setBitmap(bitmap: Bitmap) {
        overlayBitmap = bitmap
        invalidate() // Request a redraw
    }

    override fun performClick(): Boolean {
        super.performClick() // Call the superclass implementation
        // Additional logic (if needed) can go here
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        overlayBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        regionsToDraw.forEach { (region, isSelected) ->
            paint.color = if (isSelected) 0xFF00FF00.toInt() else 0xFCFCFCFC.toInt() // Green or White

            // Reuse the preallocated RectF and set its values
            rect.set(
                region.bounds.left.toFloat(),
                region.bounds.top.toFloat(),
                region.bounds.right.toFloat(),
                region.bounds.bottom.toFloat()
            )
            canvas.drawRect(rect, paint)

            // Draw text near the rectangle (below it)
            val nutritionInfo = region.nutritionInfo
            val text = "${nutritionInfo.name} ${nutritionInfo.calories} ${nutritionInfo.nutrients}"
            val x = region.bounds.left.toFloat()
            val y = region.bounds.bottom + 40f // Slightly below the rectangle
            canvas.drawText(text, x, y, textPaint)
        }
    }

    fun setRegions(regions: List<Pair<FoodRegion, Boolean>>) {
        regionsToDraw.clear()
        regionsToDraw.addAll(regions)
        invalidate() // Redraw the view
    }

    fun clearRegions() {
        regionsToDraw.clear()
        invalidate() // Redraw the view
    }
}
