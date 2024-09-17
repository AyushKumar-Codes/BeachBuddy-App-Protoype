package com.prototype.beach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class ActivitiesAdaptor(
    private val activitiesList: MutableList<String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ActivitiesAdaptor.MyViewHolder>() {

    // Map linking activity names to their respective icons
    private val activityIconMap = mapOf(
        "Jet Skiing" to R.drawable.jetskix,    // Replace with your actual drawable resource IDs
        "Banana Boat Ride" to R.drawable.bananaboatx,
        "Speed Boat Ride" to R.drawable.speedboatx,
        "Kayaking" to R.drawable.kayakingx,
        "Beach volleyball" to R.drawable.volleyballx,
        "Go Karting" to R.drawable.kartingx,
        "Kannagi Statue" to R.drawable.statuex,
        "Flying kites" to R.drawable.kitex,
        "Horse Riding" to R.drawable.horsex,
        "Sun bath" to R.drawable.sunbathx
    )

    // Interface for click listener
    interface OnItemClickListener {
        fun onItemChecked(activity: String, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_activites, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val activity = activitiesList[position]
        holder.textView.text = activity

        // Set the icon based on the activity name using the map
        val iconResId = activityIconMap[activity] ?: R.drawable.act // Use a default icon if not found
        val iconDrawable = ContextCompat.getDrawable(holder.itemView.context, iconResId)

        // Ensure the drawable is not null before setting it
        if (iconDrawable != null) {
            holder.toggleButton.icon = iconDrawable
        } else {
            // Log or handle the missing icon case
            holder.toggleButton.icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.act)
        }

        // Add listener for toggle button group
        holder.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (checkedId == holder.toggleButton.id) {
                listener.onItemChecked(activity, isChecked)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.toggleactivity) // Ensure this ID matches your layout
        val toggleGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleactivitygroup) // Ensure this ID matches your layout
        val toggleButton: MaterialButton = toggleGroup.findViewById(R.id.toggleactivity) // Ensure this ID matches your layout
    }
}
