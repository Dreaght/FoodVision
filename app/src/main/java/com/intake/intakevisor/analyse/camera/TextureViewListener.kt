package com.intake.intakevisor.analyse.camera

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import com.intake.intakevisor.analyse.Frame

class TextureViewListener(
    private val cameraController: CameraController
) : TextureView.SurfaceTextureListener {

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        Log.d("CameraController", "TextureView dimensions (onSurfaceTextureAvailable): $width x $height")

        if (cameraController.isCameraStarted) {
            if (cameraController.initialViewWidth == null || cameraController.initialViewHeight == null) {
                cameraController.initialViewWidth = width
                cameraController.initialViewHeight = height
                Log.d("CameraController", "Stored initial dimensions: $width x $height")
            }
            cameraController.openCamera(width, height)
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        Log.d("CameraController", "TextureView size changed: $width x $height")
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        cameraController.stop()
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
        val frame = Frame(surfaceTexture)
        cameraController.onFrameReceived.invoke(frame)

    }
}
