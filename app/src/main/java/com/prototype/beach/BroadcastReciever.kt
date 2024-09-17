package com.prototype.beach

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MyBroadcastReceiver", "Alarm received, starting activity")

        // Start the activity when the alarm triggers
        val activityIntent = Intent(context, BigAlertActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Required if starting an activity from background
        context?.startActivity(activityIntent)
    }
}
