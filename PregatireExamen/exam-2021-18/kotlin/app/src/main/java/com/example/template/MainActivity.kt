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

        val teacher = findViewById<Button>(R.id.teacher)
        teacher.setOnClickListener {
            val intent = Intent(this@MainActivity, TeacherActivity::class.java)
            startActivity(intent)
        }

        val student = findViewById<Button>(R.id.student)
        student.setOnClickListener {
            val intent = Intent(this@MainActivity, StudentActivity::class.java)
            startActivity(intent)
        }

        val stats = findViewById<Button>(R.id.stats)
        stats.setOnClickListener {
            val intent = Intent(this@MainActivity, StatsActivity::class.java)
            startActivity(intent)
        }
    }

}