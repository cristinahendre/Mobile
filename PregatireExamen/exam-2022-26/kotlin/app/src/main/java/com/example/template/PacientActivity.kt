package com.example.template

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.template.domain.Rezervare
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import com.tinder.scarlet.Scarlet
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.internal.http2.Settings
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit


class PacientActivity : AppCompatActivity() {

    private lateinit var nume: EditText
    private lateinit var doctor: EditText
    private lateinit var data: EditText
    private lateinit var ora: EditText
    private lateinit var detalii: EditText
    private lateinit var toSave:Rezervare
    private lateinit var button: Button
    private val model: Model by viewModels()
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
        nume = findViewById(R.id.nume)
        data = findViewById(R.id.data)
        ora = findViewById(R.id.ora)
        detalii = findViewById(R.id.detalii)
        doctor = findViewById(R.id.doctor)
        button = findViewById(R.id.button_save)
//        establisher.establishConnection()

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val rez = Rezervare(
                    0, nume.text.toString(), doctor.text.toString(), data.text.toString().toInt(),
                    ora.text.toString().toInt(), detalii.text.toString(), false, 0
                )
                if (rez.nume == "" || rez.nume == " " || rez.doctor == "" || rez.doctor == " ") {

                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {

                        progress.show()
                        val response = model.add(rez)
                        logd("after saving $response")
                        if (response != "off") {
                            val myObj = deserialize(response)
                            if (myObj != null) {
                                rez.id = myObj.id
                                rez.status= myObj.status
                                personViewModel.insert(rez)
                                //displayFinalMessage(rez)
                                displayServersMessage(myObj.id)
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            } else {
                                showErrorMessage("Error when saving.")
                                progress.dismiss()
                            }
                        } else {
                            toSave =rez
                            showErrorMessage("The server is off.")
                            progress.dismiss()
                        }
                        progress.dismiss()
                    }
                }
            } catch (e: NumberFormatException) {
                showErrorMessage("Invalid fields.")
                progress.dismiss()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.retry -> {
                logd("retry clicked")
                GlobalScope.launch(Dispatchers.Main) {
                    progress.show()
                    val response = model.add(toSave)
                    logd("after saving $response")
                    if (response != "off") {
                        val myObj = deserialize(response)
                        if (myObj != null) {
                            toSave.id = myObj.id
                            toSave.status= myObj.status
                            personViewModel.insert(toSave)
                            displayServersMessage(myObj.id)
                            finish()
                        } else {
                            showErrorMessage("Error when saving.")
                            progress.dismiss()
                        }
                    } else {
                        showErrorMessage("The server is still off.")
                        progress.dismiss()
                    }
                    progress.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showErrorMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun displayFinalMessage(rezervare: Rezervare) {
        val text = "Rezervarea pacientului ${rezervare.nume} cu doctorul ${rezervare.doctor} " +
                " se va desfasura in ziua ${rezervare.data} , la ora ${rezervare.ora} si are" +
                " detaliile ${rezervare.detalii}"
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun displayServersMessage(id:Int){
        Toast.makeText(this, "Saved the entity with id: $id", Toast.LENGTH_SHORT).show()

    }

    private fun deserialize(myString: String): Rezervare? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("changed") - 3
            val status = getStringFromArea(indexStart, indexStop, myString).toBoolean()
            return Rezervare(id, "", "", 0, 0, "", status, 0)
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

