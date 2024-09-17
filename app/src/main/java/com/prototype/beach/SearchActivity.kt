package com.prototype.beach

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnMapReadyCallback
import com.prototype.beach.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(){
    // viewBinding
    private lateinit var viewBinding:ActivitySearchBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Tag", "Starting")
        super.onCreate(savedInstanceState)

    }
}