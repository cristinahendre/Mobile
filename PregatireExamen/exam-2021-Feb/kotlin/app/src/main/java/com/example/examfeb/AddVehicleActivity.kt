package com.example.examfeb

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.models.Model
import com.example.examfeb.viewmodel.VehicleViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var license: EditText
    private lateinit var status: EditText
    private lateinit var seats: EditText
    private lateinit var driver: EditText
    private lateinit var color: EditText
    private lateinit var cargo: EditText
    private lateinit var button: Button
    private val model: Model by viewModels()
    private lateinit var progress: ProgressDialog

    private lateinit var vehicleViewModel: VehicleViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_vehicle)
        vehicleViewModel = ViewModelProviders.of(this).get(VehicleViewModel::class.java)
        license = findViewById(R.id.license)
        status = findViewById(R.id.status)
        seats = findViewById(R.id.seats)
        driver = findViewById(R.id.driver)
        color = findViewById(R.id.color)
        cargo = findViewById(R.id.cargo)
        button = findViewById(R.id.button_save)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            progress.show()
            var vehicle = Vehicle(
                0, license.text.toString(), status.text.toString(),
                seats.text.toString().toInt(), driver.text.toString(), color.text.toString(),
                cargo.text.toString().toInt(), 0
            )
            GlobalScope.launch(Dispatchers.Main) {
                val resp = model.add(vehicle)
                logd("My response in add $resp")
                if (resp != null) {
                    if (resp == "off") {
                        //the server is down
                        displayMessageToast("The server is down.")
                        vehicle.changed = 1
                        vehicleViewModel.insert(vehicle)
                        setResult(Activity.RESULT_OK, replyIntent)
                        progress.dismiss()
                        finish()
                    } else {
                        val id = getId(resp)
                        logd("id computed $id")
                        if (id == -1) {
                            displayMessageToast(resp)
                            progress.dismiss()
                        } else {
                            vehicle.changed = 0
                            vehicle.id = id
                            vehicleViewModel.insert(vehicle)

                            val msg = "The driver " + getDriver(resp) + " whose car has " +
                                    getSeats(resp) + " received the license: " + getLicense(resp)
                            logd(msg)
                            displayMessageToast(msg)
                            setResult(Activity.RESULT_OK, replyIntent)
                            progress.dismiss()
                            finish()
                        }
                    }
                } else {
                    displayMessageToast("There is some trouble.")
                    progress.dismiss()
                }
            }
        }

    }

    private fun showErrorMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun getId(message: String): Int {

        val placeDouaPuncte = message.indexOf("=")
        val placeVirgula = message.indexOf(",")
        if (placeDouaPuncte == -1 || placeVirgula == -1) {
            return -1
        }
        var myValue = ""
        for (i in message.indices) {
            if (i in (placeDouaPuncte + 1) until placeVirgula) {
                myValue += message[i]
            }
        }
        return myValue.toInt()

    }

    private fun getSeats(message: String): Int {

        val placeDouaPuncte = message.indexOf("seats") + 6
        val placeVirgula = message.indexOf("driver") -3
        if (placeDouaPuncte == -1 || placeVirgula == -1) {
            return -1
        }
        var myValue = ""
        for (i in message.indices) {
            if (i in placeDouaPuncte..placeVirgula) {
                myValue += message[i]
            }
        }
        return myValue.toInt()

    }

    private fun displayMessageToast(myMessage: String) {

        Toast.makeText(this, myMessage, Toast.LENGTH_SHORT).show()
    }

    private fun getLicense(message: String): String {

        val placeDouaPuncte = message.indexOf("license") + 8
        val placeVirgula = message.indexOf("status") - 2
        if (placeDouaPuncte == -1 || placeVirgula == -1) {
            return ""
        }
        var myValue = ""
        for (i in message.indices) {
            if (i in placeDouaPuncte until placeVirgula) {
                myValue += message[i]
            }
        }
        return myValue

    }

    private fun getDriver(message: String): String {

        val placeDouaPuncte = message.indexOf("driver") + 7
        val placeVirgula = message.indexOf("color") - 2
        if (placeDouaPuncte == -1 || placeVirgula == -1) {
            return ""
        }
        var myValue = ""
        for (i in message.indices) {
            if (i in placeDouaPuncte until placeVirgula) {
                myValue += message[i]
            }
        }
        return myValue

    }

    companion object {
        const val LICENSE = "license"
        const val SEATS = "seats"
        const val STATUS = "status"
        const val DRIVER = "driver"
        const val COLOR = "color"
        const val CARGO = "cargo"
    }
}

