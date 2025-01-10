package com.intake.intakevisor

import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.intake.intakevisor.analyse.FoodFragment
import com.intake.intakevisor.analyse.FoodProcessor
import com.intake.intakevisor.analyse.FoodRegion
import com.intake.intakevisor.analyse.Frame
import com.intake.intakevisor.analyse.camera.CameraController
import com.intake.intakevisor.analyse.util.RegionRenderer
import com.intake.intakevisor.analyse.widget.TransparentOverlayView
import com.intake.intakevisor.ui.main.MainActivity
import java.io.ByteArrayOutputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraPreview: TextureView
    private lateinit var transparentOverlay: TransparentOverlayView
    private lateinit var cameraController: CameraController
    private lateinit var galleryPreview: ImageView
    private lateinit var captureButton: Button
    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    private lateinit var regionRenderer: RegionRenderer
    private var foodProcessor: FoodProcessor? = null

    private val GALLERY_REQUEST_CODE = 1001

    private var captureMode: Boolean = false

    private var mealType: String? = null
    private var selectedDate: String? = null
    private var sessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initializeIntentData()
        initializeUIElements()
        setupUI()
        loadLastPhotoIntoPreview()
        startCameraPreview()

        cameraPreview.viewTreeObserver.addOnPreDrawListener {
            updateOverlaySize()
            true
        }
    }

    private fun initializeIntentData() {
        mealType = intent.getStringExtra("meal_type")
        selectedDate = intent.getStringExtra("selected_date")
        sessionId = intent.getStringExtra("session_id")

        Log.d("CameraActivity", "received selected date: $mealType $selectedDate")
    }

    private fun initializeUIElements() {
        cameraPreview = findViewById(R.id.cameraPreview)
        transparentOverlay = findViewById(R.id.transparent_overlay)
        galleryPreview = findViewById(R.id.gallery_preview)
        captureButton = findViewById(R.id.capture_button)
        cancelButton = findViewById(R.id.cancel_button)
        confirmButton = findViewById(R.id.confirm_button)
        regionRenderer = RegionRenderer(transparentOverlay)
    }

    private fun setupUI() {
        setupButtonListeners()
        setupOverlayTouchListener()
    }

    private fun setupButtonListeners() {
        captureButton.setOnClickListener { handleCaptureButton() }
        cancelButton.setOnClickListener { handleCancelButton() }
        confirmButton.setOnClickListener { handleConfirmButton() }
        galleryPreview.setOnClickListener { openGallery() }
    }

    private fun setupOverlayTouchListener() {
        transparentOverlay.setOnTouchListener { click, event ->
            handleOverlayTouch(event)
            true
            click.performClick()
        }
    }

    private fun handleOverlayTouch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val tappedRegion = detectTappedRegion(event.x, event.y)
            tappedRegion?.let {
                regionRenderer.toggleRegionSelection(it)
                updateConfirmButtonState()
            }

            transparentOverlay.performClick()

            updateButtonVisibilityForCaptureMode()
        }
    }

    private fun updateButtonVisibilityForCaptureMode() {
        if (captureMode) {
            if (regionRenderer.hasSelectedRegions()) {
                confirmButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
            } else {
                confirmButton.visibility = View.GONE
                cancelButton.visibility = View.VISIBLE
            }
        }
    }

    private fun openGallery() {
        captureMode = true
        cameraController.pause()
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data ?: return
            handleImageSelection(imageUri)
        }
    }

    private fun handleImageSelection(imageUri: Uri) {
        updateUIForImageSelection()
        processImageFromGallery(imageUri)
    }

    private fun updateUIForImageSelection() {
        galleryPreview.visibility = View.GONE
        captureButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
        confirmButton.visibility = View.GONE
    }

    private fun processImageFromGallery(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        foodProcessor = FoodProcessor(Frame(bitmap))

        val detectedFoods = foodProcessor?.detectFoods() ?: emptyList()
        regionRenderer.setRegions(detectedFoods)

        transparentOverlay.setBitmap(bitmap)
    }

    private fun detectTappedRegion(x: Float, y: Float): FoodRegion? {
        return regionRenderer.getDetectedRegions().firstOrNull { region ->
            region.bounds.contains(x.toInt(), y.toInt())
        }
    }

    private fun updateConfirmButtonState() {
        confirmButton.visibility =
            if (regionRenderer.hasSelectedRegions()) View.VISIBLE else View.GONE
    }

    private fun handleCaptureButton() {
        updateUIForCaptureMode()

        cameraController.pause()
        val frame = cameraController.getCurrentFrame() ?: return
        processCapturedImage(frame)
    }

    private fun updateUIForCaptureMode() {
        captureMode = true
        captureButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE
        confirmButton.visibility = View.GONE
        galleryPreview.visibility = View.GONE
    }

    private fun processCapturedImage(frame: Frame) {
        foodProcessor = FoodProcessor(frame)
        val detectedFoods = foodProcessor?.detectFoods() ?: emptyList()
        regionRenderer.setRegions(detectedFoods)
        transparentOverlay.clearBitMap()
        transparentOverlay.setBitmap(frame.image as Bitmap)
    }

    private fun handleCancelButton() {
        resetUIForDefaultMode()
        clearCaptureState()
    }

    private fun resetUIForDefaultMode() {
        captureMode = false
        captureButton.visibility = View.VISIBLE
        cancelButton.visibility = View.GONE
        confirmButton.visibility = View.GONE
        galleryPreview.visibility = View.VISIBLE
    }

    private fun clearCaptureState() {
        cameraController.resume()
        regionRenderer.clearRegions()
        transparentOverlay.clearBitMap()
        foodProcessor = null
    }

    private fun handleConfirmButton() {
        if (regionRenderer.hasSelectedRegions()) {
            val foodFragments = createFoodFragments(regionRenderer.getSelectedRegions())
            startDiary(foodFragments)
        }
    }

    private fun createFoodFragments(selectedRegions: List<FoodRegion>): ArrayList<FoodFragment> {
        val foodFragments = ArrayList<FoodFragment>()
        for (region in selectedRegions) {
            foodFragments.add(createFoodFragment(region))
        }
        return foodFragments
    }

    private fun createFoodFragment(region: FoodRegion): FoodFragment {
        val stream = ByteArrayOutputStream()
        region.fragment.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()

        return FoodFragment(image = imageBytes, nutritionInfo = region.nutritionInfo)
    }

    private fun startDiary(foodFragments: ArrayList<FoodFragment>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putParcelableArrayListExtra("food_fragments", foodFragments)
            putExtra("meal_type", mealType)
            putExtra("selected_date", selectedDate)
            putExtra("session_id", sessionId)
        }
        startActivity(intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (captureMode) handleCancelButton() else startDiary(ArrayList())
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun startCameraPreview() {
        cameraController = CameraController(applicationContext, cameraPreview) {}
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

                val lastPhotoUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Glide.with(this).load(lastPhotoUri).placeholder(R.drawable.transparent_square).into(galleryPreview)
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
            transparentOverlay.requestLayout()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraController.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!captureMode) cameraController.resume()
    }
}
