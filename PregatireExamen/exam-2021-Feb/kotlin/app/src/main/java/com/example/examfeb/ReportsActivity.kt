package com.example.examfeb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class ReportsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val topvehicles = findViewById<Button>(R.id.tenVehicles)
        topvehicles.setOnClickListener {
            val intent = Intent(this@ReportsActivity, TenVehiclesActivity::class.java)
            startActivity(intent)
        }

        val topdrivers = findViewById<Button>(R.id.tenDrivers)
        topdrivers.setOnClickListener {
            val intent = Intent(this@ReportsActivity, TenDriversActivity::class.java)
            startActivity(intent)
        }

        val fiveBiggest = findViewById<Button>(R.id.biggestCars)
        fiveBiggest.setOnClickListener {
            val intent = Intent(this@ReportsActivity, FiveBiggestVehiclesActivity::class.java)
            startActivity(intent)
        }
    }


}