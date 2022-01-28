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

        val magazioner = findViewById<Button>(R.id.magazioner)
        magazioner.setOnClickListener {
            val intent = Intent(this@MainActivity, MagazionerActivity::class.java)
            startActivity(intent)
        }

        val client = findViewById<Button>(R.id.client)
        client.setOnClickListener {
            val intent = Intent(this@MainActivity, ClientActivity::class.java)
            startActivity(intent)
        }
    }

}