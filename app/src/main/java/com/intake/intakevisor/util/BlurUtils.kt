package com.intake.intakevisor.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object BlurUtils {
    fun blur(context: Context, image: Bitmap, radius: Int): Bitmap {
        val renderScript = RenderScript.create(context)

        val input = Allocation.createFromBitmap(renderScript, image)
        val output = Allocation.createTyped(renderScript, input.type)

        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setRadius(radius.toFloat())
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.forEach(output)

        output.copyTo(image)
        renderScript.destroy()

        return image
    }
}
