package com.intake.intakevisor.welcome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.intake.intakevisor.R

class GenderImageAdapter(
    private val images: List<Int>,
    private var selectedPosition: Int = 0 // Keeps track of the currently selected position
) : RecyclerView.Adapter<GenderImageAdapter.GenderImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gender_image, parent, false)
        return GenderImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenderImageViewHolder, position: Int) {
        val currentImage = images[position]

        Glide.with(holder.itemView.context)
            .load(currentImage)
            .apply(RequestOptions().centerInside()) // Center the image inside the view without cropping
            .transition(DrawableTransitionOptions.withCrossFade(300)) // Smooth transition
            .into(holder.imageView)

        // Set click listener to update the selected position
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify adapter to update the visuals for both the old and new selected items
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = images.size

    /**
     * Updates the selected position and refreshes the RecyclerView.
     */
    fun updateSelectedPosition(newPosition: Int) {
        selectedPosition = newPosition
    }

    class GenderImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
}
