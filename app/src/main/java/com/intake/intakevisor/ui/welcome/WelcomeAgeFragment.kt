package com.intake.intakevisor.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intake.intakevisor.databinding.WelcomeAgeFragmentBinding

class WelcomeAgeFragment : Fragment() {

    private var _binding: WelcomeAgeFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var welcomeActivity: WelcomeActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WelcomeAgeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        welcomeActivity = requireActivity() as WelcomeActivity

        setupUI()
        setupDatePicker()
    }

    private fun setupUI() {
        binding.welcomeBackBtn.setOnClickListener {
            welcomeActivity.supportFragmentManager.popBackStack()
        }

        binding.welcomeDoneBtn.setOnClickListener {
            welcomeActivity.finishWelcome()
        }
    }

    private fun setupDatePicker() {
        val (year, month, day) = welcomeActivity.userData.birthDate.split("-").map { it.toInt() }

        binding.birthDatePicker.updateDate(year, month - 1, day) // Month is 0-indexed
        binding.birthDatePicker.setOnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            welcomeActivity.userData.birthDate = selectedDate
        }
    }
}
