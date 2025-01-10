package com.intake.intakevisor.ui.main.feedback

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.intake.intakevisor.databinding.CalendarDaySelectorBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.intake.intakevisor.R
import com.intake.intakevisor.ui.main.MainActivity
import com.intake.intakevisor.ui.main.diary.DiaryFragment

@RequiresApi(Build.VERSION_CODES.O)
class ReportDateDialogFragment : DialogFragment() {

    private var _binding: CalendarDaySelectorBinding? = null
    private val binding get() = _binding!!

    private var chosenDaysRange: ReportDaysRange? = null
    private var isDaysRangeSelected = false

    private var onDaysRangeChosen: ((ReportDaysRange) -> Unit)? = null
    private lateinit var calendarAdapter: CalendarAdapter

    private var currentMonth: YearMonth = YearMonth.now() // Keep track of the displayed month

    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.RoundedDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarDaySelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monthYearText = binding.calendarView.monthYearText
        val calendarRecyclerView = binding.calendarView.calendarRecyclerView

        // Initialize the calendar view
        updateCalendar()

        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)

        binding.previousMonthBtn.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            updateCalendar()
        }

        binding.nextMonthBtn.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            updateCalendar()
        }

        binding.calendarDoneBtn.setOnClickListener {
            if (selectedStartDate != null && selectedEndDate != null) {
                chosenDaysRange = ReportDaysRange(selectedStartDate!!, selectedEndDate!!)
                isDaysRangeSelected = true
                dismiss()
                onDaysRangeChosen?.invoke(chosenDaysRange!!)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendar() {
        val monthYearText = binding.calendarView.monthYearText
        val calendarRecyclerView = binding.calendarView.calendarRecyclerView

        // Update month-year text
        monthYearText.text = getString(
            R.string.month_year_text_label,
            currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()), currentMonth.year
        )

        // Generate days for the current month
        val days = generateCalendarDays(currentMonth)
        calendarAdapter = CalendarAdapter(days, selectedStartDate, selectedEndDate) { start, end ->
            selectedStartDate = start
            selectedEndDate = end
        }

        calendarRecyclerView.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateCalendarDays(month: YearMonth): List<LocalDate?> {
        val daysInMonth = month.lengthOfMonth()
        val firstDayOfMonth = month.atDay(1).dayOfWeek.value % 7 // Adjust for Sunday=0, Saturday=6
        val totalCells = 42 // 6 rows × 7 days
        val calendarDays = mutableListOf<LocalDate?>()

        for (i in 1..totalCells) {
            if (i <= firstDayOfMonth || i > firstDayOfMonth + daysInMonth) {
                calendarDays.add(null) // Empty cell
            } else {
                calendarDays.add(month.atDay(i - firstDayOfMonth))
            }
        }
        return calendarDays
    }

    fun setOnDaysRangeChosenListener(listener: (ReportDaysRange) -> Unit) {
        onDaysRangeChosen = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isDaysRangeSelected) {
            (activity as MainActivity).loadFragment((activity as MainActivity).previousFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
