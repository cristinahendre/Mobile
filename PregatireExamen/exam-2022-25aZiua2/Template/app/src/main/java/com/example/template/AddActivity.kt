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
import com.example.template.domain.Produs
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var nume: EditText
    private lateinit var tip: EditText
    private lateinit var pret: EditText
    private lateinit var cantitate: EditText
    private lateinit var discount: EditText
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
        tip = findViewById(R.id.tip)
        cantitate = findViewById(R.id.cantitate)
        pret = findViewById(R.id.pret)
        discount = findViewById(R.id.discount)
        button = findViewById(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val product = Produs(
                    0, nume.text.toString(), tip.text.toString(),
                    cantitate.text.toString().toInt(), pret.text.toString().toInt(),
                    discount.text.toString().toInt(), false, 0
                )
                if (product.nume == "" || product.nume == " " || product.discount < 0 || product.discount > 100) {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        progress.show()
                        val response = model.add(product)
                        logd(" after saving $response")
                        if (response != "off") {
                            val myObj = deserialize(response)
                            if (myObj != null) {
                                product.status =true
                                personViewModel.insert(product)
                                progress.dismiss()

                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            } else {
                                showErrorMessage("Product already exists!")
                                progress.dismiss()

                            }
                        } else {
                            product.changed = 1
                            personViewModel.insert(product)
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


    private fun deserialize(myString: String): Produs? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            return Produs(id, "", "", 0, 0, 0, false, 0)
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

