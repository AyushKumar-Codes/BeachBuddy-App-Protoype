package com.prototype.beach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

class SuggestionAdapter(
    private val context: Context,
    private val listener: OnToggleClickListener
) : RecyclerView.Adapter<SuggestionAdapter.MyViewHolder>() {

    // Define an interface to communicate with the activity
    interface OnToggleClickListener {
        fun onToggleClick(position: Int, isChecked: Boolean)
    }

    // Step 1: Maintain the state of each toggle button
    private val toggleStates = MutableList(11) { false } // Initialize with false (unchecked)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggestions, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Set the toggle button texts based on the position
        when (position) {
            0 -> {
                holder.toggleButton.textOn = "Hotels"
                holder.toggleButton.textOff = "Hotels"
            }
            1 -> {
                holder.toggleButton.textOn = "Hospitals & Clinics"
                holder.toggleButton.textOff = "Hospitals & Clinics"
            }
            2 -> {
                holder.toggleButton.textOn = "Swimming"
                holder.toggleButton.textOff = "Swimming"
            }
            3 -> {
                holder.toggleButton.textOn = "ATMS"
                holder.toggleButton.textOff = "ATMS"
            }
            4 -> {
                holder.toggleButton.textOn = "Parking"
                holder.toggleButton.textOff = "Parking"
            }

            5 -> {
                holder.toggleButton.textOn = "Toilets"
                holder.toggleButton.textOff = "Toilets"
            }
            6 -> {
                holder.toggleButton.textOn = "Drinking Water"
                holder.toggleButton.textOff = "Drinking Water"
            }
            7 -> {
                holder.toggleButton.textOn = "Restaurants"
                holder.toggleButton.textOff = "Restaurants"
            }

        }

        // Step 2: Set the toggle button state based on the stored state
        holder.toggleButton.setOnCheckedChangeListener(null)
        holder.toggleButton.isChecked = toggleStates[position]

        // Step 3: Update the state and notify the listener when the toggle button is clicked
        holder.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            toggleStates[position] = isChecked // Update the state
            listener.onToggleClick(position, isChecked) // Notify the activity
        }
    }

    override fun getItemCount(): Int {
        return 8
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toggleButton: ToggleButton = itemView.findViewById(R.id.toggleButton)
    }
}
