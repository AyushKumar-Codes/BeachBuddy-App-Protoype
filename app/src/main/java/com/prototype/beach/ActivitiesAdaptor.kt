package com.prototype.beach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup

class ActivitiesAdaptor(
    private val activitiesList: MutableList<String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ActivitiesAdaptor.MyViewHolder>() {

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

        // Add listener for toggle button group
        holder.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == holder.toggleButton.id) {
                listener.onItemChecked(activity, isChecked)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.toggleactivity) // Update with your actual TextView ID
        val toggleGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.toggleactivitygroup) // Toggle group from your layout
        val toggleButton: View = toggleGroup.findViewById(R.id.toggleactivity) // Actual toggle button inside the group
    }
}
