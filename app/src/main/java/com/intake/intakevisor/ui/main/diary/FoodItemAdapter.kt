package com.intake.intakevisor.ui.main.diary

import OnSwipeTouchListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intake.intakevisor.R
import com.intake.intakevisor.adapter.ItemDismissAdapter

class FoodItemAdapter(
    internal val foodItemList: MutableList<FoodItem>,
    private val onDeleteClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>(), ItemDismissAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_item, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = foodItemList[position]
        holder.foodName.text = holder.itemView.context.getString(
            R.string.nutrition_info, foodItem.nutrition.name, foodItem.nutrition.calories
        )

        Glide.with(holder.foodIcon.context)
            .load(foodItem.image) // Replace with your image path or bitmap
            .placeholder(R.drawable.food_icon_placeholder) // Placeholder image
            .override(100, 100) // Resize image
            .into(holder.foodIcon)
    }

    override fun getItemCount(): Int = foodItemList.size

    override fun onItemDismiss(position: Int) {
        Log.d("FoodItemAdapter", "onItemDismiss called with position: $position")

        val foodItem = foodItemList[position]
        onDeleteClick(foodItem)
        foodItemList.removeAt(position)
        notifyItemRemoved(position);
    }

    inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodIcon: ImageView = itemView.findViewById(R.id.foodIcon)
    }
}
