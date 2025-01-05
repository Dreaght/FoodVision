package com.intake.intakevisor.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.WelcomeGoalsFragmentBinding

class WelcomeGoalsFragment : Fragment() {

    private var _binding: WelcomeGoalsFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var welcomeActivity: WelcomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WelcomeGoalsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeActivity = requireActivity() as WelcomeActivity

        setupUI()
    }

    private fun setupUI() {
        binding.weightGoalSeekBar.progress = welcomeActivity.userData.goalWeight
        binding.weightGoalTitle.text = getString(R.string.weightGoalTitle, welcomeActivity.userData.goalWeight)

        binding.selectedGenderImage.setImageResource(when (welcomeActivity.userData.gender) {
            "Male" -> R.drawable.man
            "Female" -> R.drawable.woman
            else -> R.drawable.man
        })

        binding.welcomeBackBtn.setOnClickListener {
            welcomeActivity.supportFragmentManager.popBackStack()
        }

        binding.welcomeNextBtn.setOnClickListener {
            welcomeActivity.loadFragment(WelcomeAgeFragment())
        }

        binding.weightGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightGoalSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightGoalTitle.text = getString(R.string.weightGoalTitle, validProgress)
                welcomeActivity.userData.goalWeight = validProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
