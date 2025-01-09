package com.intake.intakevisor.ui.main.feedback

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.intake.intakevisor.databinding.FeedbackFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import com.intake.intakevisor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

class FeedbackFragment : Fragment() {
    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    // Flag to track if a week has been selected
    private var isWeekSelected = false

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
        ViewCompat.setOnApplyWindowInsetsListener(binding.tvSelectedWeek) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val cameraCutout = insets.displayCutout?.safeInsetTop ?: 0

            // Set margin to max(status bar height, cutout height, default 20dp)
            val marginTop = maxOf(statusBarHeight, cameraCutout, dpToPx(20))

            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = marginTop
            }

            insets
        }

        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mainActivity.activateItemInMenu(this)

        setupUI()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        // Open the dialog only if a week has not been selected
        if (!isWeekSelected) {
            val existingDialog =
                parentFragmentManager.findFragmentByTag("ReportDateDialogFragment") as? ReportDateDialogFragment
            if (existingDialog == null) { // Prevent multiple dialogs
                val dialog = ReportDateDialogFragment()

                dialog.setOnDaysRangeChosenListener { chosenDaysRange ->
                    // Handle the chosen days range
                    Log.d("FeedbackFragment", "Chosen days range: $chosenDaysRange")
                    isWeekSelected = true // Set the flag to true
                    binding.tvSelectedWeek.text = getString(
                        R.string.selectedWeekLabel,
                        "${chosenDaysRange.start.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )} - ${chosenDaysRange.end.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )}"
                    )
                    loadReportFor(chosenDaysRange)
                }

                dialog.show(parentFragmentManager, "ReportDateDialogFragment")
            } else {
                Log.d("FeedbackFragment", "Dialog already shown.")
            }
        } else {
            Log.d("FeedbackFragment", "Dialog not shown because a week is already selected.")
        }
    }

    private fun loadReportFor(reportDaysRange: ReportDaysRange) {
        showLoading(true)

        lifecycleScope.launch {
            val reportImage = withContext(Dispatchers.IO) {
                reportAPI.fetchReport(reportDaysRange)
            }
            showReport(reportImage)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.fragmentNutritionReport.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.fragmentNutritionReport.reportImageView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showReport(bitmap: Bitmap) {
        binding.fragmentNutritionReport.reportImageView.setImageBitmap(bitmap)
        binding.downloadReportButton.visibility = View.VISIBLE
        showLoading(false)
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
