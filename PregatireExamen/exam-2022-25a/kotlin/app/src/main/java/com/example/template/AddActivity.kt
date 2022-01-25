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
    private lateinit var cantitate: EditText
    private lateinit var pret: EditText
    private lateinit var discount: EditText
    private var id: Int = 0
    private lateinit var button: Button
    private val model: Model by viewModels()
    private var isUpdate = false
    private lateinit var progress: ProgressDialog
    private lateinit var myViewModel: MyViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        nume = findViewById(R.id.nume)
        tip = findViewById(R.id.tip)
        pret = findViewById(R.id.pret)
        cantitate = findViewById(R.id.cantitate)
        discount = findViewById(R.id.discount)
        button = findViewById(R.id.button_save)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val product = Produs(
                    0, nume.text.toString(), tip.text.toString(),
                    cantitate.text.toString().toInt(), pret.text.toString().toInt(),
                    discount.text.toString().toInt(), false, 0
                )
                if (product.nume == " " || product.nume == "" || product.tip == "" || product.tip == " " ||
                        product.discount <0 || product.discount >100) {
                    showErrorMessage("Date invalide.")
                    progress.dismiss()
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {
                            progress.show()

                            product.id = id
                            val resp = model.update(product)
                            if (resp == "off") {
                                showErrorMessage("The server is down.")
                                product.changed = 3
                            }
                            myViewModel.update(product)
                            progress.dismiss()

                        } else {
                            progress.show()
                            val response = model.add(product)
                            logd("id after saving $response")
                            if (response != "off") {
                                val myObj = deserialize(response)
                                if (myObj != null) {
                                    product.id = myObj.id
                                    product.status = myObj.status
                                }
                                else{
                                    showErrorMessage("Product already exists!")
                                    progress.dismiss()
                                }
                                myViewModel.insert(product)
                                displaySuccessMessage(product)
                                progress.dismiss()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            } else {
                                product.changed = 1
                                myViewModel.insert(product)
                                progress.dismiss()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                            progress.dismiss()
                        }


                    }
                }
            } catch (e: NumberFormatException) {
                showErrorMessage("Date invalide.")
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

    private fun displaySuccessMessage(product:Produs){
        val text ="Produsul cu numele ${product.nume} si pretul ${product.pret} " +
                " si tipul ${product.tip} a fost salvat in cantitatea ${product.cantitate}" +
                " cu discountul ${product.discount}"
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Produs? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.length-1
            val status = getStringFromArea(indexStart, indexStop, myString).toBoolean()
            return Produs(id,"","",0,0,0,status,0)
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

