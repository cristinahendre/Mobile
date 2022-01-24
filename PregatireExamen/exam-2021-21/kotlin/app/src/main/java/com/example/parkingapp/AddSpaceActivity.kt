package com.example.parkingapp

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
import com.example.parkingapp.domain.Space
import com.example.parkingapp.viewmodel.NetworkModel
import com.example.parkingapp.viewmodel.SpaceViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddSpaceActivity : AppCompatActivity() {

    private lateinit var number: EditText
    private lateinit var status: EditText
    private lateinit var count: EditText
    private lateinit var address: EditText
    private lateinit var button: Button
    private val model: NetworkModel by viewModels()
    private lateinit var progress: ProgressDialog
    private lateinit var spaceViewModel: SpaceViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        spaceViewModel = ViewModelProviders.of(this).get(SpaceViewModel::class.java)
        number = findViewById(R.id.number)
        status = findViewById(R.id.status)
        address = findViewById(R.id.address)
        count = findViewById(R.id.count)

        button = findViewById(R.id.button_save)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            progress.show()
            try {
                val space = Space(
                    0, number.text.toString(), address.text.toString(),
                    status.text.toString(), count.text.toString().toInt(), 0
                )
                GlobalScope.launch(Dispatchers.Main) {
                    val resp = model.add(space)
                    logd("My response in add $resp")
                    if (resp != null) {
                        if (resp == "off") {
                            //the server is down
                            displayMessageToast("The server is down.")
                            space.changed = 1
                            spaceViewModel.insert(space)
                            setResult(Activity.RESULT_OK, replyIntent)
                            progress.dismiss()
                            finish()
                        } else {
                            val myObj = deserializeSpace(resp)
                            logd("object after deserialization $myObj")
                            if (myObj == null) {
                                logd("it was null")
                                displayMessageToast(resp)
                                progress.dismiss()
                            } else {
                                myObj.address = space.address
                                myObj.number = space.number
                                space.id= myObj.id
                                val msg =
                                    "The space from " + space.address + " with the number " +
                                            space.number + " has the status: " + space.status +
                                            " and count: " + space.count
                                logd(msg)
                                spaceViewModel.insert(space)
                                displayMessageToast(msg)
                                setResult(Activity.RESULT_OK, replyIntent)
                                progress.dismiss()
                                finish()

                            }
                        }
                    } else {
                        displayMessageToast("There is some unkown trouble.")
                        progress.dismiss()
                    }
                }
            } catch (e: NumberFormatException) {
                showErrorMessage("Invalid data fields.")
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

    private fun deserializeSpace(myString: String): Space? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("number") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("count") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("count") + 6
            indexStop = myString.indexOf("changed") - 3
            val count = getStringFromArea(indexStart, indexStop, myString).toInt()

            return Space(id, "", "", status, count, 0)
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

    private fun displayMessageToast(myMessage: String) {

        Toast.makeText(this, myMessage, Toast.LENGTH_SHORT).show()
    }


}

