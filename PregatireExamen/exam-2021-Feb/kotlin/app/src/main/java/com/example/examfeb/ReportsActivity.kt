package com.example.examfeb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)


        val registration = findViewById<Button>(R.id.tenVehicles)
        registration.setOnClickListener {
            val intent = Intent(this@ReportsActivity, TenVehiclesActivity::class.java)
            startActivity(intent)
        }

    }

}