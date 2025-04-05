package com.intake.intakevisor.listener

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.ui.main.diary.FoodItemAdapter

class SwipeToDeleteCallback(private val adapter: FoodItemAdapter) : ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val foodItem = adapter.foodItemList[position]
        if (direction == ItemTouchHelper.LEFT) {
            // Reveal delete button
            adapter.notifyItemChanged(position) // Trigger UI update if needed
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Hide delete button
            adapter.notifyItemChanged(position) // Trigger UI update if needed
        }
    }
}
