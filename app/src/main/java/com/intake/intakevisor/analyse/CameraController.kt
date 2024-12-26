package com.intake.intakevisor.analyse

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.hardware.camera2.*
import android.content.Context
import android.util.Log
import android.os.Handler
import android.os.Looper

class CameraController(
    private val preview: TextureView,
    private val onFrameReceived: (Frame?) -> Unit
) {

    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var cameraCaptureRequest: CaptureRequest
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private lateinit var surface: Surface // Use Surface here instead of SurfaceTexture
    private val cameraManager: CameraManager = preview.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            surface = Surface(surfaceTexture) // Create Surface from SurfaceTexture
            openCamera() // Open the camera after surface is available
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            stop() // Stop the camera if surface is destroyed
            return true
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
            // Here you can capture frames or pass data to the callback
            val frame = Frame(surfaceTexture)  // Use surfaceTexture to create a Frame object
            onFrameReceived(frame)  // Pass the frame to onFrameReceived
        }
    }

    init {
        preview.surfaceTextureListener = textureListener
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val cameraId = cameraManager.cameraIdList.firstOrNull { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            facing == CameraCharacteristics.LENS_FACING_BACK
        }

        if (cameraId != null) {
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            cameraManager.openCamera(cameraId, cameraStateCallback, handler)
        } else {
            Log.e("CameraController", "No back-facing camera found")
        }
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            val surfaceTexture = preview.surfaceTexture
            surfaceTexture?.setDefaultBufferSize(preview.width, preview.height)
            surface = Surface(surfaceTexture) // Use Surface here for preview

            val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice != null) {
                            cameraCaptureSession = session
                            cameraCaptureRequest = captureRequestBuilder.build()
                            cameraCaptureSession.setRepeatingRequest(cameraCaptureRequest, null, handler)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("CameraController", "Configuration failed")
                    }
                },
                handler
            )
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e("CameraController", "Camera error: $error")
        }
    }

    fun start() {
        if (preview.isAvailable) {
            openCamera()
        } else {
            preview.surfaceTextureListener = textureListener
        }
    }

    fun stop() {
        try {
            cameraCaptureSession.stopRepeating()
            cameraDevice.close()
        } catch (e: Exception) {
            Log.e("CameraController", "Error while stopping camera: ${e.message}")
        }
    }
}
