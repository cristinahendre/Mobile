package com.example.examfeb

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.examfeb.viewmodel.VehicleViewModel
import com.google.android.material.snackbar.Snackbar


class DriverActivity : AppCompatActivity() {

    private lateinit var driver: EditText
    private lateinit var progress: ProgressDialog

    private lateinit var vehicleViewModel: VehicleViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        vehicleViewModel = ViewModelProviders.of(this).get(VehicleViewModel::class.java)
        driver = findViewById(R.id.driver)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)

        val button = findViewById<Button>(R.id.choose)
        button.setOnClickListener {
            val replyIntent = Intent()
            progress.show()
            if(driver.text.toString() =="" || driver.text.toString()==" ") {
                showErrorMessage("The driver's name is empty.")
            }
            else{
                val intent = Intent(applicationContext, ViewVehiclesActivity::class.java)
                intent.putExtra("Driver", driver.text.toString())
                startActivity(intent)
                finish()
            }
            progress.dismiss()

            }


    }

    private fun showErrorMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

}

