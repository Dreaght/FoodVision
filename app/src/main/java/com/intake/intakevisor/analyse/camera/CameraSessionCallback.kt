package com.intake.intakevisor.analyse.camera

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.util.Log

class CameraSessionCallback(
    private val captureRequestBuilder: CaptureRequest.Builder,
    private val cameraController: CameraController
) : CameraCaptureSession.StateCallback() {

    override fun onConfigured(session: CameraCaptureSession) {
        cameraController.cameraCaptureSession = session
        cameraController.cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, cameraController.handler)
    }

    override fun onConfigureFailed(session: CameraCaptureSession) {
        Log.e("CameraController", "Configuration failed")
    }
}
