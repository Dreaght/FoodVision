package com.intake.intakevisor.analyse.camera

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.hardware.camera2.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.MediaRecorder
import android.util.Log
import android.os.Handler
import android.os.Looper
import com.intake.intakevisor.analyse.Frame
import java.nio.ByteBuffer

class CameraController(
    private val preview: TextureView,
    val onFrameReceived: (Frame?) -> Unit
) {

    lateinit var cameraDevice: CameraDevice
    lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var surface: Surface
    private val cameraManager: CameraManager = preview.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val handler = Handler(Looper.getMainLooper())

    var isCameraStarted = false
    private var previewSize: Size? = null
    private var cameraId: String? = null
    var initialViewWidth: Int? = null
    var initialViewHeight: Int? = null

    private val previewSizeManager = PreviewSizeManager()

    init {
        preview.surfaceTextureListener = TextureViewListener(this)
    }

    @SuppressLint("MissingPermission")
    fun openCamera(viewWidth: Int, viewHeight: Int) {
        cameraManager.cameraIdList.firstOrNull()?.let { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val videoSizes = configs?.getOutputSizes(MediaRecorder::class.java)

            videoSizes?.let {
                val bestPreviewSize = previewSizeManager.getBestPreviewSize(configs.getOutputSizes(SurfaceTexture::class.java), viewWidth, viewHeight)
                bestPreviewSize?.takeIf { previewSize != it }?.apply {
                    previewSize = this
                    adjustTextureViewSize(this)
                }

                previewSize?.let { size ->
                    val surfaceTexture = preview.surfaceTexture
                    surfaceTexture?.setDefaultBufferSize(size.width, size.height)
                    surface = Surface(surfaceTexture)
                    cameraManager.openCamera(cameraId, CameraStateCallback(surface, handler, this), handler)
                }
            }
        } ?: run {
            Log.e("CameraController", "No cameras found")
        }
    }

    private fun adjustTextureViewSize(size: Size) {
        val aspectRatio = size.width.toFloat() / size.height
        val screenRatio = preview.width.toFloat() / preview.height

        val newWidth: Int
        val newHeight: Int
        if (aspectRatio > screenRatio) {
            newWidth = preview.width
            newHeight = (preview.width / aspectRatio).toInt()
        } else {
            newHeight = preview.height
            newWidth = (preview.height * aspectRatio).toInt()
        }

        preview.layoutParams = preview.layoutParams.apply {
            width = newWidth
            height = newHeight
        }
        preview.requestLayout()
    }

    fun start() {
        if (!isCameraStarted) {
            isCameraStarted = true
            initialViewWidth?.let { openCamera(it, initialViewHeight ?: it) }
        }
    }

    fun stop() {
        if (isCameraStarted) {
            isCameraStarted = false
            try {
                cameraCaptureSession.stopRepeating()
                cameraDevice.close()
            } catch (e: Exception) {
                Log.e("CameraController", "Error while stopping camera: ${e.message}")
            }
        }
    }

    fun pause() {
        stop()
    }

    fun resume() {
        if (!isCameraStarted) {
            start()
            initialViewWidth?.let { width ->
                initialViewHeight?.let { height ->
                    previewSize?.let { size ->
                        if (preview.width != size.width || preview.height != size.height) {
                            adjustTextureViewSize(size)
                        }
                        preview.surfaceTexture?.apply {
                            setDefaultBufferSize(preview.width, preview.height)
                            surface = Surface(this)
                        }
                        if (!::cameraDevice.isInitialized) {
                            createCaptureSession()
                            openCamera(preview.width, preview.height)
                        }
                    }
                }
            }
        }
    }

    private fun createCaptureSession() {
        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(surface)
        }

        cameraDevice.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    try {
                        session.setRepeatingRequest(previewRequestBuilder.build(), null, handler)
                    } catch (e: CameraAccessException) {
                        Log.e("CameraController", "Error starting camera preview: ${e.message}")
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e("CameraController", "Camera capture session configuration failed")
                }
            },
            handler
        )
    }

    fun getCurrentFrame(): Frame? {
        return Frame(preview.bitmap as Bitmap)
    }
}
