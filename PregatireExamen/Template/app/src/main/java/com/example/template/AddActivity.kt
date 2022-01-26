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
import com.example.template.domain.Person
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var age: EditText
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
        age = findViewById(R.id.age)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isUpdate = true
            logd("update window")
            name.setText(bundle.getString("Name"))
            id = bundle.getInt("Id")
            age.setText(bundle.getInt("Age").toString())
            button.text = "Update"
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val person = Person(0, name.text.toString(), age.text.toString().toInt(), 0)
                if (person.name == "" || person.name == " " || person.age < 0) {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {
                            progress.show()

                            person.id = id
                            val resp = model.update(person)
                            if (resp == "off") {
                                showErrorMessage("The server is down.")
                                person.changed = 3
                            }
                            personViewModel.update(person)
                            progress.dismiss()

                        } else {
                            progress.show()
                            val response = model.add(person)
                            logd("id after saving $response")
                            if (response != "off") {
                                val myObj = deserialize(response)
                                if (myObj != null) {
                                    person.id = myObj.id
                                    personViewModel.insert(person)
                                    displayFinalMessage(myObj)
                                }
                                else{
                                    showErrorMessage("Error when saving.")
                                }
                            } else {
                                person.changed = 1
                                personViewModel.insert(person)
                                showErrorMessage("The server is off.")
                            }
                            progress.dismiss()
                        }
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()

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

    private fun displayFinalMessage(person: Person) {
        val text = "The person with the name ${person.name} has ${person.age} years."
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Person? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            return Person(id,"",0,0)
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

