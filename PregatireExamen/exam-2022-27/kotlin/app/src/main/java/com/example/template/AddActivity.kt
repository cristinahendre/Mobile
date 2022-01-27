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
    private lateinit var media1: EditText
    private lateinit var media2: EditText
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
        media1 = findViewById(R.id.media1)
        media2 = findViewById(R.id.media2)
        button = findViewById(R.id.button_save)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val valoare1 = 0.75
                val valoare2 = 0.25
                val dosar = Dosar(
                    0, nume.text.toString(), media1.text.toString().toInt(),
                        media2.text.toString().toInt(), 0, false, 0
                )
                dosar.medie = (valoare1 * dosar.medie1 + valoare2 * dosar.medie2).toInt()
                if (dosar.nume == "" || dosar.nume == " ") {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        progress.show()
                        val response = model.add(dosar)
                        logd("id after saving $response")
                        if (response != "off") {

                            val myObj =deserialize(response)
                            if(myObj == null){
                                showErrorMessage("File already exists!")
                                progress.dismiss()
                            }
                            else {
                                dosar.id = myObj.id
                                personViewModel.insert(dosar)
                                displayFinalMessage(myObj.id)
                                progress.dismiss()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }

                        } else {
                            dosar.changed = 1
                            personViewModel.insert(dosar)
                            showErrorMessage("The server is off.")
                            progress.dismiss()
                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()
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

    private fun showErrorMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun displayFinalMessage(resp: Int) {

        Toast.makeText(this, resp.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Dosar? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            return Dosar(id, "", 0, 0, 0, false, 0)
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

