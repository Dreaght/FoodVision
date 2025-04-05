package com.intake.intakevisor.ui.main.feedback

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.FeedbackFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import com.intake.intakevisor.ui.main.feedback.api.BackendReportAPI
import com.intake.intakevisor.ui.main.feedback.api.ReportAPI
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class FeedbackFragment : Fragment() {
    private var _binding: FeedbackFragmentBinding? = null
    private val binding get() = _binding!!

    private var reportJob: Job? = null // Job to track the coroutine

    lateinit var mainActivity: MainActivity
    var isWeekSelected = false

    private var isDialogShown = false // Flag to track dialog state

    private lateinit var reportAPI: ReportAPI

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

        reportAPI = BackendReportAPI()

        setupUI()
    }

    fun setupUI() {
        if (!isAdded) {
            return
        }

        if (!isWeekSelected && !isDialogShown) { // Ensure the dialog isn't already shown
            showDialog()
        }

        binding.downloadReportButton.setOnClickListener {
            val drawable = binding.fragmentNutritionReport.reportImageView.drawable
            if (drawable != null && drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                saveBitmapToGallery(bitmap)
            } else {
                Log.e("FeedbackFragment", "No image to save.")
            }
        }

    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "report_${System.currentTimeMillis()}.png"
        val mimeType = "image/png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/IntakeReports") // Custom folder
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = requireContext().contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            // Optional: user feedback
            Toast.makeText(requireContext(), "Report saved to gallery! Path: Pictures/IntakeReports", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(requireContext(), "Failed to save image.", Toast.LENGTH_SHORT).show()
        }
    }

    fun showDialog() {
        isDialogShown = true // Set the flag to true before showing the dialog

        val dialog = ReportDateDialogFragment()
        dialog.apply {
            setOnDaysRangeChosenListener { chosenDaysRange ->
                Log.d("FeedbackFragment", "Chosen days range: $chosenDaysRange")
                isWeekSelected = true

                // Format the start and end dates of the Calendar range
                val startDate = chosenDaysRange.start.time
                val endDate = chosenDaysRange.end.time

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedStartDate = dateFormat.format(startDate)
                val formattedEndDate = dateFormat.format(endDate)

                binding.tvSelectedWeek.text = getString(
                    R.string.selectedWeekLabel,
                    "$formattedStartDate - $formattedEndDate"
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
                    reportAPI.fetchReport(reportDaysRange, requireContext())
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
