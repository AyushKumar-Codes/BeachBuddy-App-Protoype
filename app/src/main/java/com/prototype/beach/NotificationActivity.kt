package com.prototype.beach

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
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

        Log.d("Notification", "Retrieving the mainNotificationManager from MainActivity")
        notificationManager = MainActivity.DataRepository.mainNotificationManager

        Log.d("Notification", "Received ${notificationsList.size} notifications")

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)

        initRecycler()
        initHelpButton()
    }


    private fun initRecycler(){
        Log.d("Notification", "In initRecycler with ${notificationsList.size} notifications")

        NotificationRecyclerView = viewBinding.notificationRecyclerView
        NotificationRecyclerView.layoutManager = LinearLayoutManager(this)
        NotificationRecyclerView.setHasFixedSize(false)

        NotificationRecyclerView.adapter =
            NotificationAdapter(notificationsList) { selectedNotification ->
                Log.d("Notification", "Selected notification with title : ${selectedNotification.title}")
            }

        notificationAdapter = NotificationRecyclerView.adapter as NotificationAdapter

        notificationAdapter.notifyDataSetChanged()
    }

    private fun initHelpButton(){
        viewBinding.helpButton.setImageResource(R.drawable.call_emergency)

        var mockPhoneNumber = 1111111111

        viewBinding.helpButton.setOnClickListener {
            Log.d("HelpButton", "Clicked on Help Button")

            // Create an intent to open the dialer
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${mockPhoneNumber}")
            }

            // Start the activity to open the dialer
            startActivity(intent)
        }

    }
}