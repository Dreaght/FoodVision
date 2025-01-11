package com.intake.intakevisor.ui.main.feedback

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.FeedbackFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import kotlinx.coroutines.*
import java.time.format.DateTimeFormatter

class FeedbackFragment : Fragment() {
    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    private var reportJob: Job? = null // Job to track the coroutine

    lateinit var mainActivity: MainActivity
    var isWeekSelected = false

    private var isDialogShown = false // Flag to track dialog state

    private val reportAPI = ReportAPI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FeedbackFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mainActivity.activateItemInMenu(this)

        setupUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupUI() {
        if (!isAdded) {
            return
        }

        if (!isWeekSelected && !isDialogShown) { // Ensure the dialog isn't already shown
            showDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDialog() {
        isDialogShown = true // Set the flag to true before showing the dialog

        val dialog = ReportDateDialogFragment()
        dialog.apply {
            setOnDaysRangeChosenListener { chosenDaysRange ->
                Log.d("FeedbackFragment", "Chosen days range: $chosenDaysRange")
                isWeekSelected = true
                binding.tvSelectedWeek.text = getString(
                    R.string.selectedWeekLabel,
                    "${chosenDaysRange.start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${chosenDaysRange.end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                )
                loadReportFor(chosenDaysRange)
                (activity as MainActivity).feedbackDialogShown = false
            }
        }

        dialog.show(childFragmentManager, "ReportDateDialogFragment")

        // Reset the flag when the dialog is dismissed
        dialog.setOnDismissListener {
            isDialogShown = false
        }
    }

    private fun loadReportFor(reportDaysRange: ReportDaysRange) {
        showLoading(true)

        reportJob = lifecycleScope.launch {
            try {
                val reportImage = withContext(Dispatchers.IO) {
                    reportAPI.fetchReport(reportDaysRange)
                }
                if (isAdded && _binding != null) { // Ensure fragment is attached and binding is valid
                    showReport(reportImage)
                }
            } catch (e: CancellationException) {
                Log.d("FeedbackFragment", "Loading canceled.")
            } catch (e: Exception) {
                Log.e("FeedbackFragment", "Error loading report: ${e.message}")
            } finally {
                if (isAdded && _binding != null) {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) { // Check if binding is valid
            binding.fragmentNutritionReport.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.fragmentNutritionReport.reportImageView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showReport(bitmap: Bitmap) {
        if (_binding != null) { // Check if binding is valid
            binding.fragmentNutritionReport.reportImageView.setImageBitmap(bitmap)
            binding.downloadReportButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        reportJob?.cancel()
    }
}
