package com.example.template

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button

class EmployeeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        val level = findViewById<Button>(R.id.level)
        level.setOnClickListener {
            val intent = Intent(this@EmployeeActivity, LevelActivity::class.java)
            startActivity(intent)
        }

        val time = findViewById<Button>(R.id.time)
        time.setOnClickListener {
            val intent = Intent(this@EmployeeActivity, TimeActivity::class.java)
            startActivity(intent)
        }
    }

}