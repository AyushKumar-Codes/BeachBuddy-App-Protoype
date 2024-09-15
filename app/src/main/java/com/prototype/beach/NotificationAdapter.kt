package com.prototype.beach

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class NotificationAdapter (private var notificationsList: MutableList<NotificationClass>, private val onItemClick: (NotificationClass) -> Unit) : RecyclerView.Adapter<NotificationAdapter.NotificationAdapterHolder>() {
    init{
        Log.d("Notification", "In Top of the NotificationAdapter class with ${itemCount} notifications")
        if(notificationsList.size>1){
            notificationsList = notificationsList.reversed() as ArrayList<NotificationClass>
        }
    }

    class NotificationAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationImage: ShapeableImageView = itemView.findViewById(R.id.image)
        val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        val notificationMessage: TextView = itemView.findViewById(R.id.notificationMessage)
        val notificationBackgroundColorCode: View = itemView.findViewById(R.id.imageBox)
        val notificationConstraintLayer: ConstraintLayout = itemView.findViewById(R.id.notificationItemConstraintLayout)
        val notificationDeleteButton: Button = itemView.findViewById(R.id.deleteButton)

        var notificationManager = MainActivity.DataRepository.mainNotificationManager

        init{
            Log.d("Notification", "Assigned views to variables in adapter holder")
        }

        fun bind(notification: NotificationClass, onItemClick: (NotificationClass) -> Unit, position: Int, adapter: NotificationAdapter) {
            notificationTitle.text = notification.title
            notificationImage.setImageResource(notification.mainImageID)

            // Handle notification click
            notificationConstraintLayer.setOnClickListener {
                Log.d("Notification", "Pressed ${notification.title} notification with message ${notification.message}")
                onItemClick(notification)
            }

            // Handle delete button click, with adapter reference
            notificationDeleteButton.setOnClickListener {
                Log.d("Notification", "Canceled notification ${notification.id} with title ${notification.title}")
                notificationManager.cancel(notification.id)


                Log.d("Notification", "Visually deleted notification ${notification.id} with title ${notification.title}")
                adapter.deleteNotification(position)  // Use adapter reference to call the functions

                MainActivity.DataRepository.deleteNotification(notification.id)

                onItemClick(notification)  // Call the callback for delete action
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationAdapterHolder {
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        Log.d("Notification", "Assigned itemview as a view inside onCreateViewHolder()")
        return NotificationAdapter.NotificationAdapterHolder(
            itemview
        )
    }

    override fun getItemCount(): Int {return notificationsList.size}

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationAdapterHolder, position: Int) {
        val currentNotification = notificationsList[position]

        holder.notificationImage.setImageResource(currentNotification.mainImageID)
        holder.notificationTitle.text = currentNotification.title
        holder.notificationMessage.text = currentNotification.message

        val backgroundRes = when (currentNotification.colorCode) {
            "blue" -> R.drawable.notification_background_blue
            "red" -> R.drawable.notification_background_red
            "green" -> R.drawable.notification_background_green
            else -> R.drawable.question
        }

        holder.notificationBackgroundColorCode.setBackgroundResource(backgroundRes)



        holder.bind(currentNotification, onItemClick, position, this)

        Log.d("Notification", "Added Notification recyclerItem with id ${currentNotification.id}")
    }

    fun setFilteredList(filteredList: List<NotificationClass>) {
        Log.d("Notification", "In setFilteredList() with ${itemCount} notifications")
        Log.d("Notification", "In setFilteredList(), filteredList.size: ${filteredList.size}")
        Log.d("Notification", "In setFilteredList(), notificationsList.size: ${notificationsList.size}")

        notificationsList.clear()  // Clear the current list
        notificationsList.addAll(filteredList)  // Add the filtered list

        Log.d("Notification", "In setFilteredList(), notificationsList.size: ${notificationsList.size}")

        for(notification in notificationsList){
            Log.d("Notification", "Added Notification with id ${notification.id}")
        }
        notifyDataSetChanged()  // Notify the RecyclerView to refresh
    }

    fun deleteNotification(position: Int) {
        // Remove the item from the data source (notificationsList)
        notificationsList.removeAt(position)

        // Notify the adapter that an item has been removed
        notifyItemRemoved(position)

        // Notify the adapter about item range changes (optional but recommended)
        notifyItemRangeChanged(position, notificationsList.size)
    }
}
