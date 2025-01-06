package com.intake.intakevisor.ui.main.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.R

class FoodItemAdapter(private val foodItemList: List<FoodItem>) :
    RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_item, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = foodItemList[position]
        holder.foodName.text = foodItem.name

        if (foodItem.image.width == 0 || foodItem.image.height == 0) {
            holder.foodIcon.setImageResource(R.drawable.food_icon_placeholder) // Use a default placeholder
        } else {
            holder.foodIcon.setImageBitmap(foodItem.image)
        }

        holder.foodIcon.setImageBitmap(foodItem.image)
    }

    override fun getItemCount(): Int = foodItemList.size

    inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodIcon: ImageView = itemView.findViewById(R.id.foodIcon)
    }
}
