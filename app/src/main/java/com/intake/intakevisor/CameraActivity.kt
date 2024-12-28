package com.intake.intakevisor

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.intake.intakevisor.analyse.*
import com.intake.intakevisor.analyse.camera.CameraController
import com.intake.intakevisor.analyse.util.RegionRenderer
import com.intake.intakevisor.analyse.widget.TransparentOverlayView

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraPreview: TextureView
    private lateinit var transparentOverlay: TransparentOverlayView
    private lateinit var cameraController: CameraController
    private lateinit var galleryPreview: ImageView
    private lateinit var captureButton: Button
    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    private lateinit var regionRenderer: RegionRenderer
    private var foodProcessor: FoodProcessor? = null // Holds the processor state for the session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraPreview = findViewById(R.id.cameraPreview)
        transparentOverlay = findViewById(R.id.transparent_overlay)
        galleryPreview = findViewById(R.id.gallery_preview)
        captureButton = findViewById(R.id.capture_button)
        cancelButton = findViewById(R.id.cancel_button)
        confirmButton = findViewById(R.id.confirm_button)

        regionRenderer = RegionRenderer(transparentOverlay) // Pass the preview as the render view

        setupUI()
        loadLastPhotoIntoPreview()
        startCameraPreview()

        cameraPreview.viewTreeObserver.addOnPreDrawListener {
            updateOverlaySize()
            true // Continue drawing
        }
    }

    private fun setupUI() {
        captureButton.setOnClickListener { handleCaptureButton() }
        cancelButton.setOnClickListener { handleCancelButton() }
        confirmButton.setOnClickListener {
            if (regionRenderer.hasSelectedRegions()) {
                val selectedRegions = regionRenderer.getSelectedRegions()
                // Handle selected regions here (e.g., process or save them)
            }
        }

        transparentOverlay.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val tappedRegion = detectTappedRegion(event.x, event.y)
                tappedRegion?.let {
                    regionRenderer.toggleRegionSelection(it)
                    updateConfirmButtonState()
                }
                transparentOverlay.performClick() // Trigger performClick for accessibility
                if (regionRenderer.hasSelectedRegions()) {
                    confirmButton.visibility = View.VISIBLE
                    cancelButton.visibility = View.GONE
                } else {
                    confirmButton.visibility = View.GONE
                    cancelButton.visibility = View.VISIBLE
                }
            }
            true // Indicate the touch event was handled
        }
    }

    private fun detectTappedRegion(x: Float, y: Float): FoodRegion? {
        // Determine if the tapped coordinates match any region
        return regionRenderer.getDetectedRegions().firstOrNull { region ->
            region.bounds.contains(x.toInt(), y.toInt())
        }
    }

    private fun updateConfirmButtonState() {
        confirmButton.visibility =
            if (regionRenderer.hasSelectedRegions()) View.VISIBLE else View.GONE
    }

    private fun handleCaptureButton() {
        captureButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
        confirmButton.visibility = View.GONE
        galleryPreview.visibility = View.GONE

        cameraController.pause()
        val frame = cameraController.getCurrentFrame() ?: return
        foodProcessor = FoodProcessor(frame)

        val detectedFoods = foodProcessor?.detectFoodRegions() ?: emptyList()
        regionRenderer.setRegions(detectedFoods)
    }

    private fun handleCancelButton() {
        captureButton.visibility = View.VISIBLE
        cancelButton.visibility = View.GONE
        confirmButton.visibility = View.GONE
        galleryPreview.visibility = View.VISIBLE

        cameraController.resume()
        regionRenderer.clearRegions()
        foodProcessor = null // Clear the processor state
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleCancelButton()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun startCameraPreview() {
        cameraController = CameraController(cameraPreview) { currentFrame ->
            // Frame processing for the preview
        }
        cameraController.start()
    }

    private fun loadLastPhotoIntoPreview() {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(collection, projection, null, null, sortOrder)

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = it.getLong(idColumn)

                val lastPhotoUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                Glide.with(this)
                    .load(lastPhotoUri)
                    .placeholder(R.drawable.transparent_square)
                    .into(galleryPreview)
            } else {
                galleryPreview.setImageResource(R.drawable.transparent_square)
            }
        }
    }

    private fun updateOverlaySize() {
        val width = cameraPreview.width
        val height = cameraPreview.height

        if (transparentOverlay.width != width || transparentOverlay.height != height) {
            transparentOverlay.layoutParams.width = width
            transparentOverlay.layoutParams.height = height
            transparentOverlay.requestLayout() // Reapply layout
        }
    }
}
