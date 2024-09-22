package com.prototype.beach

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.prototype.beach.databinding.ActivityAichatassistantBinding

class AIChatAssistantActivity : AppCompatActivity() {
    // data binding
    private lateinit var binding : ActivityAichatassistantBinding

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        // restrict screen orientation to portrait only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_aichatassistant)
    }
}