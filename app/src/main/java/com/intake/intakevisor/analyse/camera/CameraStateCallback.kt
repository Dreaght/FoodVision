package com.intake.intakevisor.analyse.camera

import android.hardware.camera2.CameraDevice
import android.os.Handler
import android.util.Log
import android.view.Surface

class CameraStateCallback(
    private val surface: Surface,
    private val handler: Handler,
    private val cameraController: CameraController
) : CameraDevice.StateCallback() {

    override fun onOpened(camera: CameraDevice) {
        cameraController.cameraDevice = camera

        val captureRequestBuilder = cameraController.cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)

        cameraController.cameraDevice.createCaptureSession(
            listOf(surface),
            CameraSessionCallback(captureRequestBuilder, cameraController),
            handler
        )
    }

    override fun onDisconnected(camera: CameraDevice) {
        cameraController.cameraDevice.close()
    }

    override fun onError(camera: CameraDevice, error: Int) {
        Log.e("CameraController", "Camera error: $error")
    }
}

