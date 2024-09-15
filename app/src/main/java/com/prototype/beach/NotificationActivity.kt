package com.prototype.beach

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prototype.beach.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity(){
    private lateinit var viewBinding: ActivityNotificationBinding


    // Notification
    private lateinit var NotificationRecyclerView: RecyclerView
    private lateinit var notificationsList : MutableList<NotificationClass>
    private lateinit var notificationManager : NotificationManager
    private lateinit var notificationAdapter : NotificationAdapter


    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        notificationsList = MainActivity.DataRepository.notificationsList
        notificationManager = MainActivity.DataRepository.mainNotificationManager

        Log.d("Notification", "Recieved ${notificationsList?.size} notifications")

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)

        initRecycler()
    }

    private fun initRecycler(){
        Log.d("Notification", "In initRecycler with ${notificationsList.size} notifications")

        NotificationRecyclerView = viewBinding.notificationRecyclerView
        NotificationRecyclerView.layoutManager = LinearLayoutManager(this)
        NotificationRecyclerView.setHasFixedSize(false)

        NotificationRecyclerView.adapter =
            NotificationAdapter(notificationsList) { selectedNotification ->
                Log.d("Notification", "Selected notification with title : ${selectedNotification.title}")
                notificationManager.cancel(selectedNotification.id)
            }

        notificationAdapter = NotificationRecyclerView.adapter as NotificationAdapter
        notificationAdapter.notifyDataSetChanged()
    }
}