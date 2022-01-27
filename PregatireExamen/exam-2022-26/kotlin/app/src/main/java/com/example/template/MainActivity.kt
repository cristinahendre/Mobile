package com.example.template

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import com.example.template.viewmodel.MyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val personViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)


        val secretar = findViewById<Button>(R.id.secretar)
        secretar.setOnClickListener {
            val intent = Intent(this@MainActivity, SecretareActivity::class.java)
            startActivity(intent)
        }

        val pacient = findViewById<Button>(R.id.pacient)
        pacient.setOnClickListener {
            val intent = Intent(this@MainActivity, PacientActivity::class.java)
            startActivity(intent)
        }
    }

}