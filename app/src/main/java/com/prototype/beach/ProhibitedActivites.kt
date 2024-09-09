package com.prototype.beach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProhibitedActivites(private val activitiesList: MutableList<String>) :
    RecyclerView.Adapter<ProhibitedActivites.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prohibited_activity_text, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val activity = activitiesList[position]
        holder.textView.text = activity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.prohib) // Update with your actual TextView ID
    }
}
