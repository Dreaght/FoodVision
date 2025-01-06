package com.intake.intakevisor.ui.main.feedback

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.intake.intakevisor.databinding.ReportSelectDateBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportDateDialogFragment : DialogFragment() {

    private var _binding: ReportSelectDateBinding? = null
    private val binding get() = _binding!!
    private var onWeekSelected: ((String) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    private var isWeekSelected = false // Track whether a week was selected

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReportSelectDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press to dismiss the dialog and go back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dismiss()
                }
            }
        )

        // Generate past weeks and setup RecyclerView
        val pastWeeks = generatePastWeeks()
        val adapter = WeekAdapter(pastWeeks) { selectedWeek ->
            isWeekSelected = true // Mark that a week was selected
            onWeekSelected?.invoke(selectedWeek)
            dismiss() // Close the dialog after selection
        }

        binding.weekPickerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.weekPickerRecycler.adapter = adapter
    }

    private fun generatePastWeeks(): List<String> {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Example: "Jan 01, 2023"
        val calendar = Calendar.getInstance()
        val weeks = mutableListOf<String>()

        for (i in 0..12) {
            val weekStart = calendar.clone() as Calendar
            weekStart.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

            val weekEnd = calendar.clone() as Calendar
            weekEnd.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)

            val week = "${dateFormat.format(weekStart.time)} - ${dateFormat.format(weekEnd.time)}"
            weeks.add(week)

            calendar.add(Calendar.WEEK_OF_YEAR, -1) // Go to the previous week
        }

        return weeks
    }

    fun setOnWeekSelectedListener(listener: (String) -> Unit) {
        onWeekSelected = listener
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        // Pop back stack only if no week was selected
        if (!isWeekSelected) {
            Log.d("ReportDateDialogFragment", "No week selected, navigating back.")
            parentFragmentManager.popBackStack()
        } else {
            Log.d("ReportDateDialogFragment", "Week selected, no navigation back.")
        }
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
