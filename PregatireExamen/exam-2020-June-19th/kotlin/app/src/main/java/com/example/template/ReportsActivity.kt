package com.example.template

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button

class ReportsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val biggest5 = findViewById<Button>(R.id.biggest5)
        biggest5.setOnClickListener {
            val intent = Intent(this@ReportsActivity, Biggest5Activity::class.java)
            startActivity(intent)
        }

        val topdrivers = findViewById<Button>(R.id.topdrivers)
        topdrivers.setOnClickListener {
            val intent = Intent(this@ReportsActivity, TopDriversActivity::class.java)
            startActivity(intent)
        }

        val topvehicles = findViewById<Button>(R.id.topvehicles)
        topvehicles.setOnClickListener {
            val intent = Intent(this@ReportsActivity, TopVehiclesActivity::class.java)
            startActivity(intent)
        }
    }

}