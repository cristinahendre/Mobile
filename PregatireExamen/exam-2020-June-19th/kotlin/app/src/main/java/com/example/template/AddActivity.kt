package com.example.template

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
import com.example.template.domain.Vehicle
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var driver: EditText
    private lateinit var status: EditText
    private lateinit var capacity: EditText
    private lateinit var passengers: EditText
    private lateinit var paint: EditText
    private var id: Int = 0
    private lateinit var button: Button
    private val model: Model by viewModels()
    private var isUpdate = false
    private lateinit var progress: ProgressDialog
    private lateinit var personViewModel: MyViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        personViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        name = findViewById(R.id.name)
        driver = findViewById(R.id.driver)
        capacity = findViewById(R.id.capacity)
        status = findViewById(R.id.status)
        paint = findViewById(R.id.paint)
        passengers = findViewById(R.id.passengers)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isUpdate = true
            logd("update window")
            name.setText(bundle.getString("Name"))
            id = bundle.getInt("Id")
            passengers.setText(bundle.getInt("Age").toString())
            button.text = "Update"
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val vehicle = Vehicle(
                    0,
                    name.text.toString(),
                    status.text.toString(),
                    passengers.text.toString().toInt(),
                    driver.text.toString(),
                    paint.text.toString(),
                    capacity.text.toString().toInt(),
                    0
                )
                if (vehicle.name == "" || vehicle.name == " " || vehicle.status == "" ||
                    vehicle.status == " "
                ) {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {
                            progress.show()

                            vehicle.id = id
                            val resp = model.update(vehicle)
                            if (resp == "off") {
                                showErrorMessage("The server is down.")
                                vehicle.changed = 3
                            }
                            personViewModel.update(vehicle)
                            progress.dismiss()
                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()

                        } else {
                            progress.show()
                            val response = model.add(vehicle)
                            logd("id after saving $response")
                            if (response != "off") {
                                val myObj = deserialize(response)
                                if (myObj != null) {
                                    vehicle.status =myObj.status
                                    progress.dismiss()
                                    personViewModel.insert(vehicle)
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                } else {
                                    showErrorMessage("Error when saving.")
                                    progress.dismiss()
                                }
                            } else {
                                vehicle.changed = 1
                                personViewModel.insert(vehicle)
                                showErrorMessage("The server is off.")
                                progress.dismiss()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            progress.dismiss()
                        }


                    }
                }
            } catch (e: NumberFormatException) {
                showErrorMessage("Invalid fields.")
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

    private fun displayFinalMessage(vehicle: Vehicle) {
        val text = "The."
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Vehicle? {
//Vehicle(id=55, name=yes, status=tt, passengers=5, driver=dd, paint=f, capacity=5, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("passengers") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)
            return Vehicle(id, "", status, 0, "", "", 0, 0)
        } catch (e: NumberFormatException) {
            logd("exceptie la deserializare $e")
            return null
        }

    }

    private fun getStringFromArea(start: Int, end: Int, myMessage: String): String {
        var result = ""
        for (i in myMessage.indices) {
            if (i in start..end) result += myMessage[i]
        }
        return result
    }
}

