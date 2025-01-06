package com.intake.intakevisor.ui.main.feedback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.intake.intakevisor.databinding.FeedbackFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import com.intake.intakevisor.R

class FeedbackFragment : Fragment() {
    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    // Flag to track if a week has been selected
    private var isWeekSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FeedbackFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mainActivity.activateItemInMenu(this)

        setupUI()
    }

    private fun setupUI() {
        // Open the dialog only if a week has not been selected
        if (!isWeekSelected) {
            val existingDialog =
                parentFragmentManager.findFragmentByTag("ReportDateDialogFragment") as? ReportDateDialogFragment
            if (existingDialog == null) { // Prevent multiple dialogs
                val dialog = ReportDateDialogFragment()
                dialog.setOnWeekSelectedListener { selectedWeek ->
                    // Update the TextView with the selected week
                    binding.tvSelectedWeek.text =
                        getString(R.string.selectedWeekLabel, selectedWeek)
                    isWeekSelected = true // Set the flag to true
                    Log.d("FeedbackFragment", "Week selected: $selectedWeek")
                }
                dialog.show(parentFragmentManager, "ReportDateDialogFragment")
            } else {
                Log.d("FeedbackFragment", "Dialog already shown.")
            }
        } else {
            Log.d("FeedbackFragment", "Dialog not shown because a week is already selected.")
        }
    }

    override fun onDestroyView() {
        // Dismiss any existing dialog when the fragment view is destroyed
        val existingDialog =
            parentFragmentManager.findFragmentByTag("ReportDateDialogFragment") as? ReportDateDialogFragment
        existingDialog?.dismissAllowingStateLoss()

        super.onDestroyView()
        _binding = null
        isWeekSelected = false
    }
}
