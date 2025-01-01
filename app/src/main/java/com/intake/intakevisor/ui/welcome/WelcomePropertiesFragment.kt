package com.intake.intakevisor.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.intake.intakevisor.databinding.WelcomePropertiesFragmentBinding
import com.intake.intakevisor.R
import kotlin.math.abs

class WelcomePropertiesFragment : Fragment() {

    private var _binding: WelcomePropertiesFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var welcomeActivity: WelcomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WelcomePropertiesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeActivity = requireActivity() as WelcomeActivity

        setupUI()
        setupViewPager()
    }

    private fun setupUI() {
        binding.weightSeekBar.progress = welcomeActivity.userData.weight
        binding.weightTitle.text = getString(R.string.weightTitle, welcomeActivity.userData.weight)
        binding.ageSeekBar.progress = welcomeActivity.userData.age
        binding.ageTitle.text = getString(R.string.ageTitle, welcomeActivity.userData.age)
        binding.heightSeekBar.progress = welcomeActivity.userData.height
        binding.heightTitle.text = getString(R.string.heightTitle, welcomeActivity.userData.height)

        binding.welcomeNextBtn.setOnClickListener {
            welcomeActivity.userData.weight = binding.weightSeekBar.progress
            welcomeActivity.userData.age = binding.ageSeekBar.progress
            welcomeActivity.userData.height = binding.heightSeekBar.progress

            welcomeActivity.loadFragment(WelcomeGoalsFragment())
        }

        binding.weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum weight (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightTitle.text = getString(R.string.weightTitle, validProgress)
                welcomeActivity.userData.weight = validProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Age TextView
        binding.ageSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum age (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_AGE) UserData.MIN_AGE else progress
                binding.ageSeekBar.progress = validProgress // reset progress if it's below the min
                binding.ageTitle.text = getString(R.string.ageTitle, validProgress)
                welcomeActivity.userData.age = validProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Height TextView
        binding.heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum height (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_HEIGHT) UserData.MIN_HEIGHT else progress
                binding.heightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.heightTitle.text = getString(R.string.heightTitle, validProgress)
                welcomeActivity.userData.height = validProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.otherGender.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(welcomeActivity, R.string.other_gender_toast, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupViewPager() {
        val images = listOf(
            R.drawable.male_emblem_image,
            R.drawable.female_emblem_image
        )

        val adapter = GenderImageAdapter(images)
        binding.genderImageViewPager.adapter = adapter

        // Add a custom transformation for a smooth stack effect
        binding.genderImageViewPager.setPageTransformer { page, position ->
            val absPos = abs(position)
            page.scaleX = 1 - 0.2f * absPos
            page.scaleY = 1 - 0.2f * absPos
            page.translationX = -page.width * 0.9f * position
            page.alpha = 0.5f + (1 - absPos) * 0.5f
        }

        // Disable clipping to allow images to overlap
        binding.genderImageViewPager.clipToPadding = false
        binding.genderImageViewPager.clipChildren = false
        binding.genderImageViewPager.offscreenPageLimit = images.size // Preload all items

        // Update the adapter's selected position when the page changes
        binding.genderImageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.genderImageViewPager.post {
                    adapter.updateSelectedPosition(position)
                }
                welcomeActivity.userData.gender = if (position == 0) "Male" else "Female"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })
    }
}
