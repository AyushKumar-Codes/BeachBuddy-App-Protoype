package com.prototype.beach

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(private val placesList: MutableList<String>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<PlaceAdapter.PlaceAdapterHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAdapterHolder {
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.places_list_item, parent, false)
        return PlaceAdapterHolder(itemview)
    }

    override fun onBindViewHolder(holder: PlaceAdapterHolder, position: Int) {
        val currentPlace = placesList[position]

        holder.placeText.text = currentPlace
        holder.bind(currentPlace, onItemClick)
    }

    override fun getItemCount(): Int {return placesList.size}

    class PlaceAdapterHolder (itemview: View): RecyclerView.ViewHolder(itemview) {
        val placeText: TextView = itemView.findViewById(R.id.placeName_textView)
        val placeConstraintLayer: ConstraintLayout =
            itemView.findViewById(R.id.placesItem_constraintLayout)


        fun bind(place: String, onItemClick: (String) -> Unit) {
            // Set data for the views
            placeText.text = place

            // Set the onClickListener for the constraint layout
            placeConstraintLayer.setOnClickListener {
                Log.d("testing", "Pressed ${placeText.text}")
                onItemClick(place) // Call the callback with the selected Place
            }
        }
    }

    fun setFilteredList(filteredList: List<String>) {
        placesList.clear()  // Clear the current list
        placesList.addAll(filteredList)  // Add the filtered list
        notifyDataSetChanged()  // Notify the RecyclerView to refresh
    }
}