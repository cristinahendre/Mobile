package com.example.parkingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = findViewById<Button>(R.id.manager)
        manager.setOnClickListener {
            val intent = Intent(this@MainActivity, ManagerActivity::class.java)
            startActivity(intent)
        }

        val users = findViewById<Button>(R.id.users)
        users.setOnClickListener {
            val intent = Intent(this@MainActivity, UsersActivity::class.java)
            startActivity(intent)
        }

        val stats = findViewById<Button>(R.id.stats)
        stats.setOnClickListener {
            val intent = Intent(this@MainActivity, StatsActivity::class.java)
            startActivity(intent)
        }
    }
}