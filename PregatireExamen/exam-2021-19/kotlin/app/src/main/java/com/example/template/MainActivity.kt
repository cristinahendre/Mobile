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

        val section = findViewById<Button>(R.id.section)
        section.setOnClickListener {
            val intent = Intent(this@MainActivity, StaffActivity::class.java)
            startActivity(intent)
        }

        val employee = findViewById<Button>(R.id.employee)
        employee.setOnClickListener {
            val intent = Intent(this@MainActivity, EmployeeActivity::class.java)
            startActivity(intent)
        }
    }

}