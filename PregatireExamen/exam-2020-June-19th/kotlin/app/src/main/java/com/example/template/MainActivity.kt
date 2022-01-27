package com.example.template

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registration = findViewById<Button>(R.id.registration)
        registration.setOnClickListener {
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val manage = findViewById<Button>(R.id.manage)
        manage.setOnClickListener {
            val intent = Intent(this@MainActivity, ManageActivity::class.java)
            startActivity(intent)
        }

        val reports = findViewById<Button>(R.id.reports)
        reports.setOnClickListener {
            val intent = Intent(this@MainActivity, ReportsActivity::class.java)
            startActivity(intent)
        }

        val driver = findViewById<Button>(R.id.driver)
        driver.setOnClickListener {
            val intent = Intent(this@MainActivity, DriverActivity::class.java)
            startActivity(intent)
        }
    }

}