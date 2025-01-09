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
import kotlinx.coroutines.*
import java.time.format.DateTimeFormatter

class FeedbackFragment : Fragment() {
    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    private var reportJob: Job? = null // Job to track the coroutine

    lateinit var mainActivity: MainActivity

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
        if (!isWeekSelected) {
            val existingDialog =
                parentFragmentManager.findFragmentByTag("ReportDateDialogFragment") as? ReportDateDialogFragment
            if (existingDialog == null) {
                val dialog = ReportDateDialogFragment()

                dialog.setOnDaysRangeChosenListener { chosenDaysRange ->
                    Log.d("FeedbackFragment", "Chosen days range: $chosenDaysRange")
                    isWeekSelected = true
                    if (isAdded) { // Check if the fragment is still attached
                        binding.tvSelectedWeek.text = getString(
                            R.string.selectedWeekLabel,
                            "${chosenDaysRange.start.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )} - ${chosenDaysRange.end.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )}"
                        )
                    }
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
        // Cancel any ongoing coroutine to prevent crashes
        reportJob?.cancel()

        val existingDialog =
            parentFragmentManager.findFragmentByTag("ReportDateDialogFragment") as? ReportDateDialogFragment
        existingDialog?.dismissAllowingStateLoss()

        super.onDestroyView()
        _binding = null
        isWeekSelected = false
    }
}
