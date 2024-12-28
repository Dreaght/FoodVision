package com.intake.intakevisor.analyse.widget

import android.R
import android.content.Context
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

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f // Default stroke width
        isAntiAlias = true
    }

    private val regionsToDraw = mutableListOf<Pair<FoodRegion, Boolean>>() // FoodRegion and isSelected
    private val rect = RectF() // Preallocated RectF object to reuse

    override fun performClick(): Boolean {
        super.performClick() // Call the superclass implementation
        // Additional logic (if needed) can go here
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        regionsToDraw.forEach { (region, isSelected) ->
            paint.color = if (isSelected) 0xFF00FF00.toInt() else 0xFCFCFCFC.toInt() // Green or Gray

            // Reuse the preallocated RectF and set its values
            rect.set(
                region.bounds.left.toFloat(),
                region.bounds.top.toFloat(),
                region.bounds.right.toFloat(),
                region.bounds.bottom.toFloat()
            )
            canvas.drawRect(rect, paint)
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
