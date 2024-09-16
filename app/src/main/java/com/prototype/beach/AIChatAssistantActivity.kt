package com.prototype.beach

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.prototype.beach.databinding.ActivityAichatassistantBinding

class AIChatAssistantActivity : AppCompatActivity() {
    // data binding
    private lateinit var binding : ActivityAichatassistantBinding

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_aichatassistant)
    }
}