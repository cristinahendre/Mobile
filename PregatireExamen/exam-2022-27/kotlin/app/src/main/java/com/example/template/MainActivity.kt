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

        val statistica = findViewById<Button>(R.id.statistica)
        statistica.setOnClickListener {
            val intent = Intent(this@MainActivity, StatisticaActivity::class.java)
            startActivity(intent)
        }

        val candidat = findViewById<Button>(R.id.candidat)
        candidat.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }

        val admin = findViewById<Button>(R.id.admin)
        admin.setOnClickListener {
            val intent = Intent(this@MainActivity, AdminActivity::class.java)
            startActivity(intent)
        }
    }

}