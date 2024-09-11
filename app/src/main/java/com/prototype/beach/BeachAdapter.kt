package com.prototype.beach

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.prototype.beach.databinding.ActivitySearchBinding
import com.google.android.material.imageview.ShapeableImageView

class BeachAdapter(private val beachesList: MutableList<Beach>, private val onItemClick: (Beach) -> Unit) : RecyclerView.Adapter<BeachAdapter.BeachAdapterHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeachAdapterHolder {
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.beaches_list_item, parent, false)
        return BeachAdapterHolder(itemview)
    }

    override fun getItemCount(): Int {return beachesList.size}

    override fun onBindViewHolder(holder: BeachAdapterHolder, position: Int) {
        val currentBeach = beachesList[position]

        holder.beachImage.setImageResource(currentBeach.imageID)
        holder.beachText.text = currentBeach.name
        holder.bind(currentBeach, onItemClick)
    }

    class BeachAdapterHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val beachImage: ShapeableImageView = itemView.findViewById(R.id.titleShapeableImageView)
        val beachText: TextView = itemView.findViewById(R.id.headingTextView)
        val beachConstraintLayer: ConstraintLayout = itemView.findViewById(R.id.MainConstraintLayout)



        fun bind(beach: Beach, onItemClick: (Beach) -> Unit) {
            // Set data for the views
            beachText.text = beach.name
            beachImage.setImageResource(beach.imageID) // assuming imageID is a drawable resource ID

            // Set the onClickListener for the constraint layout
            beachConstraintLayer.setOnClickListener {
                Log.d("testing", "Pressed ${beachText.text}")
                onItemClick(beach) // Call the callback with the selected beach
            }
        }
    }

    fun setFilteredList(filteredList: List<Beach>) {
        beachesList.clear()  // Clear the current list
        beachesList.addAll(filteredList)  // Add the filtered list
        notifyDataSetChanged()  // Notify the RecyclerView to refresh
    }
}