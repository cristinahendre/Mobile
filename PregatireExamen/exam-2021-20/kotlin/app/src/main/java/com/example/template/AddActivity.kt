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
import com.example.template.domain.Item
import com.example.template.model.Model
import com.example.template.viewmodel.ItemViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var status: EditText
    private lateinit var quantity: EditText
    private lateinit var price: EditText
    private var id: Int = 0
    private lateinit var button: Button
    private val model: Model by viewModels()
    private var isUpdate = false
    private lateinit var progress: ProgressDialog
    private lateinit var personViewModel: ItemViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        personViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        name = findViewById(R.id.name)
        quantity = findViewById(R.id.quantity)
        price = findViewById(R.id.price)
        status = findViewById(R.id.status)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isUpdate = true
            logd("update window")
            name.setText(bundle.getString("Name"))
            status.setText(bundle.getString("Status"))
            id = bundle.getInt("Id")
            price.setText(bundle.getInt("Price").toString())
            quantity.setText(bundle.getInt("Quantity").toString())
            button.text = "Update"
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {
                val item = Item(
                    0, name.text.toString(), quantity.text.toString().toInt(),
                    status.text.toString(), price.text.toString().toInt(), 0
                )
                if (item.name == " " || item.name == "" || item.status == "" || item.status == " ") {
                    showErrorMessage("The name/status values are empty.")
                    if (item.price == 0 || item.quantity == 0) {
                        showErrorMessage("The price/quantity cannot be 0.")
                    }
                    progress.dismiss()
                } else {

                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {
                            progress.show()

                            item.id = id
                            val resp = model.update(item)
                            if (resp == "off") {
                                showErrorMessage("The server is down.")
                                item.changed = 3
                            }
                            personViewModel.update(item)
                            progress.dismiss()

                        } else {
                            progress.show()
                            val response = model.add(item)
                            logd("after saving $response")
                            if (response != "off") {
                                val toSave = deserialize(response)
                                logd("deserializare $toSave")
                                if (toSave != null) {
                                    item.id = toSave.id
                                    item.status = toSave.status
                                    item.price = toSave.price
                                    personViewModel.insert(item)
                                    showErrorMessage(
                                        "The item ${item.name} with" +
                                                "the price ${item.price} was saved in quantity of" +
                                                "${item.quantity}; the status is ${item.status}"
                                    )
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                } else {
                                    showErrorMessage("There was an error. This item already exists.")
                                }
                            } else {
                                showErrorMessage("The server is down.")
                                item.changed = 1
                                personViewModel.insert(item)
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

        Toast.makeText(this,myMessage,Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Item? {
        //Item(id=0, name=a, quantity=2, status=ned, price=82, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("price") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("price") + 6
            indexStop = myString.indexOf("changed") - 3
            val price = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (price == 0) {
                return null
            }
            return Item(id, "", 0, status, price, 0)
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

