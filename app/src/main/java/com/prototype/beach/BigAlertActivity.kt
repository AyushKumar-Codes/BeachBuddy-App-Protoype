package com.prototype.beach

import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.prototype.beach.databinding.ActivityBigalertnotificationBinding

class BigAlertActivity : AppCompatActivity() {
    lateinit var binding: ActivityBigalertnotificationBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator // Declare Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restrict screen orientation to portrait only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bigalertnotification)

        // Set the image for alert
        binding.alertImage.setBackgroundResource(R.drawable.exclamation)

        // Initialize the vibrator
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Play alert sound and start vibration with 5 loops
        playAlertSound()
        startVibration()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer and stop vibration when the activity is destroyed
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        // Cancel vibration if still vibrating
        if (::vibrator.isInitialized) {
            vibrator.cancel()
        }
    }

    private fun playAlertSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_audio)
        mediaPlayer.start()

        // Set up a listener to repeat the sound 5 times
        var loopCount = 0
        val maxLoops = 5
        mediaPlayer.setOnCompletionListener {
            loopCount++
            if (loopCount < maxLoops) {
                mediaPlayer.start() // Replay the sound
            } else {
                mediaPlayer.release() // Stop and release after 5 loops
                vibrator.cancel() // Stop vibration after sound stops
            }
        }
    }

    private fun startVibration() {
        if (vibrator.hasVibrator()) {
            val vibrationPattern = longArrayOf(0, 500, 1000) // Delay, Vibrate, Pause (in milliseconds)
            val repeatIndex = 1 // Start repeating from the second element in the array (vibrate-pause)

            // Vibrate using the pattern for 5 loops
            val effect = VibrationEffect.createWaveform(vibrationPattern, repeatIndex)
            vibrator.vibrate(effect)
        }
    }
}
