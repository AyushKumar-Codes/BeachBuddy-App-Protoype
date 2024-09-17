package com.prototype.beach

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.prototype.beach.databinding.ActivityBigalertnotificationBinding

class BigAlertActivity : AppCompatActivity() {
    lateinit var binding: ActivityBigalertnotificationBinding
    private lateinit var mediaPlayer: MediaPlayer // Declare MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restrict screen orientation to portrait only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bigalertnotification)

        // Set the image for alert
        binding.alertImage.setBackgroundResource(R.drawable.exclamation)

        // Play alert sound with 5 loops
        playAlertSound()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources when the activity is destroyed
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    private fun playAlertSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_audio)
        mediaPlayer.start()

        // Set up a listener to repeat the sound 5 times
        var loopCount = 0
        var maxLoops = 5
        mediaPlayer.setOnCompletionListener {
            loopCount++
            if (loopCount < maxLoops) {
                mediaPlayer.start() // Replay the sound
            } else {
                mediaPlayer.release() // Stop and release after 5 loops
            }
        }
    }
}
