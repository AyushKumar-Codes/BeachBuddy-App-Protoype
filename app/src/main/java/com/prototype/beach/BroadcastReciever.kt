package com.prototype.beach

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlertReceiver", "Alarm received! Starting BigAlertActivity...")

        // Start BigAlertActivity
        val alertIntent = Intent(context, BigAlertActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required to start activity from background
        }
        context.startActivity(alertIntent)
    }
}