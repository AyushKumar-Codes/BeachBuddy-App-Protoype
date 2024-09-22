package com.prototype.beach

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.prototype.beach.databinding.BeachGeneralBinding

class BeachActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beach_general)

        // Get any data passed from the marker click (e.g., beach name, ID)
        val beachName = intent.getStringExtra("beach_name")
        // Now you can update your UI with the passed beach data, if needed
        // For example:

        testCardrecycler()

        findViewById<TextView>(R.id.general_aboutTitleTextView).text = "About "+beachName
    }

    private fun testCardrecycler(){
        var viewBinding : BeachGeneralBinding = DataBindingUtil.setContentView(this, R.layout.beach_general)

        var card = CardObject()
        card.text = "VolleyBall"
        card.imageID = R.drawable.volleyball_ball
        card.imageName = "volleyball_ball"

        var card2 = CardObject()
        card2.text = "Swimming"
        card2.imageID = R.drawable.swimming
        card2.imageName = "swimming"

        var cardsList_Activities = mutableListOf(card, card2)

        var CardRecyclerView = viewBinding.sunTimingsRecyclerView
        CardRecyclerView.layoutManager = LinearLayoutManager(this)
        CardRecyclerView.setHasFixedSize(false)
        CardRecyclerView.adapter = CardAdapter(cardsList_Activities)
    }
}
