package com.intake.intakevisor.ui.main.feedback

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import com.intake.intakevisor.R

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(
    private val days: List<LocalDate?>,
    private var selectedStartDate: LocalDate?,
    private var selectedEndDate: LocalDate?,
    private val onDateRangeSelected: (LocalDate?, LocalDate?) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var lastToastShow: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int = days.size

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.day_text)

        fun bind(day: LocalDate?) {
            textView.text = day?.dayOfMonth?.toString() ?: ""
            textView.setTextColor(Color.BLACK)

            // Set circular background for start and end dates
            if (day != null && (day == selectedStartDate || day == selectedEndDate)) {
                textView.setBackgroundResource(R.drawable.circle_background)
            }
            // Set a different background for dates in the range
            else if (day != null && selectedStartDate != null && selectedEndDate != null &&
                day.isAfter(selectedStartDate) && day.isBefore(selectedEndDate)) {
                textView.setBackgroundColor(0xFFFFFFCE.toInt()) // Pastel-yellow color for the range
            }
            // Default background
            else {
                textView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                if (day != null) {
                    if (selectedStartDate == null || (selectedStartDate != null && selectedEndDate != null)) {
                        selectedStartDate = day
                        selectedEndDate = null
                    } else if (selectedStartDate != null && selectedEndDate == null) {
                        if (day.isBefore(selectedStartDate)) {
                            selectedEndDate = selectedStartDate
                            selectedStartDate = day
                        } else {
                            selectedEndDate = day
                        }

                        // Check if the selected range exceeds 7 days
                        if (selectedStartDate != null && selectedEndDate != null &&
                            selectedEndDate!!.minusDays(6).isAfter(selectedStartDate)) {
                            selectedEndDate = null
                            selectedStartDate = day

                            val currentTime = System.currentTimeMillis()

                            if (currentTime - lastToastShow < 2000) {
                                return@setOnClickListener
                            }

                            lastToastShow = currentTime

                            Toast.makeText(itemView.context, "Selected range exceeds 7 days.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    onDateRangeSelected(selectedStartDate, selectedEndDate)
                    notifyDataSetChanged()
                }
            }
        }
    }
}
