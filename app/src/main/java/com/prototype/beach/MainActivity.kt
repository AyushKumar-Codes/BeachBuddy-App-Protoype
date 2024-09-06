package com.prototype.beach

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.prototype.beach.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.acc.setOnClickListener {
            changeview()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
    }
    private fun changeview() {
        // Inflate the bottom sheet layout
        val bottomSheetView = layoutInflater.inflate(R.layout.acc_menu, null)
        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }

}