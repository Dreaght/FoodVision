package com.intake.intakevisor

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.intake.intakevisor.welcome.GenderImageAdapter
import kotlin.math.abs

class WelcomeActivity : BaseActivity() {

    private lateinit var genderImageViewPager: ViewPager2
    private lateinit var weightSeekBar: SeekBar
    private lateinit var ageSeekBar: SeekBar
    private lateinit var heightSeekBar: SeekBar
    private lateinit var weightTitle: TextView
    private lateinit var ageTitle: TextView
    private lateinit var heightTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        setupUI()
        setupViewPager()
    }

    fun setupUI() {
        weightSeekBar = findViewById(R.id.weightSeekBar)
        ageSeekBar = findViewById(R.id.ageSeekBar)
        heightSeekBar = findViewById(R.id.heightSeekBar)

        weightTitle = findViewById(R.id.weightTitle)
        weightTitle.text = getString(R.string.weightTitle, weightSeekBar.progress)
        ageTitle = findViewById(R.id.ageTitle)
        ageTitle.text = getString(R.string.ageTitle, ageSeekBar.progress)
        heightTitle = findViewById(R.id.heightTitle)
        heightTitle.text = getString(R.string.heightTitle, heightSeekBar.progress)

        val MIN_AGE = 1
        val MIN_HEIGHT = 50 // cm
        val MIN_WEIGHT = 10  // kg

        // Update Weight TextView
        weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum weight (e.g., can't be 0)
                val validProgress = if (progress < MIN_WEIGHT) MIN_WEIGHT else progress
                weightSeekBar.progress = validProgress // reset progress if it's below the min
                weightTitle.text = getString(R.string.weightTitle, validProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Age TextView
        ageSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum age (e.g., can't be 0)
                val validProgress = if (progress < MIN_AGE) MIN_AGE else progress
                ageSeekBar.progress = validProgress // reset progress if it's below the min
                ageTitle.text = getString(R.string.ageTitle, validProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Height TextView
        heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum height (e.g., can't be 0)
                val validProgress = if (progress < MIN_HEIGHT) MIN_HEIGHT else progress
                heightSeekBar.progress = validProgress // reset progress if it's below the min
                heightTitle.text = getString(R.string.heightTitle, validProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupViewPager() {
        genderImageViewPager = findViewById(R.id.genderImageViewPager)

        val images = listOf(
            R.drawable.male_image, // Replace with actual male image resource
            R.drawable.female_image // Replace with actual female image resource
        )

        val adapter = GenderImageAdapter(images)
        genderImageViewPager.adapter = adapter

        // Add a custom transformation for a smooth stack effect
        genderImageViewPager.setPageTransformer { page, position ->
            val absPos = abs(position)
            page.scaleX = 1 - 0.2f * absPos
            page.scaleY = 1 - 0.2f * absPos
            page.translationX = -page.width * 0.9f * position
            page.alpha = 0.5f + (1 - absPos) * 0.5f
        }

        // Disable clipping to allow images to overlap
        genderImageViewPager.clipToPadding = false
        genderImageViewPager.clipChildren = false
        genderImageViewPager.offscreenPageLimit = images.size // Preload all items

        // Update the adapter's selected position when the page changes
        genderImageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.updateSelectedPosition(position)
            }
        })

        // Notify adapter of data changes to ensure proper initialization
        adapter.notifyDataSetChanged()
    }

}
