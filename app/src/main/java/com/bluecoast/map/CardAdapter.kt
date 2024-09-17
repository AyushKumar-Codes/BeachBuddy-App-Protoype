package com.prototype.beach

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val cardsList:MutableList<CardObject>) : RecyclerView.Adapter<CardAdapter.CardAdapterHolder>(){
    class CardAdapterHolder(itemView : View): RecyclerView.ViewHolder(itemView){

        // define what elements can be directly accessed by the adapter
        val card : CardView = itemView.findViewById(R.id.card_cardView)
        val cardBottomText : TextView = itemView.findViewById(R.id.card_Text)
        val cardImage : ImageView = itemView.findViewById(R.id.card_Image)


        init {
            // Set an OnClickListener for the card
            card.setOnClickListener {
                Log.d("testing", "Pressed ${cardBottomText.text}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardAdapterHolder(itemView)
    }

    override fun getItemCount(): Int {return cardsList.size}

    override fun onBindViewHolder(holder: CardAdapterHolder, position: Int) {
        val currentCard = cardsList[position]

        holder.cardImage.setImageResource(currentCard.imageID)
        holder.cardBottomText.text = currentCard.text

//    fun setFilteredList(filteredList: List<Beach>) {
//        beachesList.clear()  // Clear the current list
//        beachesList.addAll(filteredList)  // Add the filtered list
//        notifyDataSetChanged()  // Notify the RecyclerView to refresh
    }
}