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

        val items = findViewById<Button>(R.id.items)
        items.setOnClickListener {
            val intent = Intent(this@MainActivity, ItemsActivity::class.java)
            startActivity(intent)
        }

        val shopping = findViewById<Button>(R.id.shopping)
        shopping.setOnClickListener {
            val intent = Intent(this@MainActivity, ShoppingActivity::class.java)
            startActivity(intent)
        }

        val stats = findViewById<Button>(R.id.stats)
        stats.setOnClickListener {
            val intent = Intent(this@MainActivity, StatsActivity::class.java)
            startActivity(intent)
        }

    }



}