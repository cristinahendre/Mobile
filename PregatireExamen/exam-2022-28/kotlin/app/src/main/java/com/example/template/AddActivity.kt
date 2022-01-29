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
import com.example.template.domain.Dosar
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var nume: EditText
    private lateinit var medie: EditText
    private lateinit var etaj: EditText
    private lateinit var orientare: EditText
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
        medie = findViewById(R.id.medie)
        etaj = findViewById(R.id.etaj)
        orientare= findViewById(R.id.orientare)
        button = findViewById(R.id.button_save)

        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val dosar = Dosar(0, nume.text.toString(),medie.text.toString().toInt(),
                    etaj.text.toString().toInt(),orientare.text.toString().toBoolean(),
                    "",false,0)
                if (dosar.nume == "" || dosar.nume == " " ) {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                            progress.show()
                            val response = model.add(dosar)
                            logd("after saving $response")
                            if (response != "off") {
                                val myObj = deserialize(response)
                                if (myObj != null) {
                                    displayToastMessage("De la server! ${myObj.id}")
                                    dosar.status = myObj.status
                                    dosar.camera = myObj.camera
                                    personViewModel.insert(dosar)
                                    progress.dismiss()
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                }
                                else{
                                    showErrorMessage("File already exists!")
                                    progress.dismiss()
                                }
                            } else {
                                dosar.changed = 1
                                personViewModel.insert(dosar)
                                progress.dismiss()
                                displayToastMessage("The server is off.")
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            progress.dismiss()
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

    private fun displayToastMessage(myMessage: String) {
        Toast.makeText(this, myMessage, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Dosar? {

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

            indexStart = myString.indexOf("camera") + 7
            indexStop = myString.indexOf("status") - 3
            val camera = getStringFromArea(indexStart, indexStop, myString)
            return Dosar(id,"",0,0,false,camera,status,0)
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

