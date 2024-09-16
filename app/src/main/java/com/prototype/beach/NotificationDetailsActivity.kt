package com.prototype.beach

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class NotificationDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)  // Add a layout if needed

        // Log when the activity is started
        Log.d("Notification", "Activity started from notification")
    }
}