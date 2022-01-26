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
import com.example.template.domain.Rule
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var level: EditText
    private lateinit var status: EditText
    private lateinit var from: EditText
    private lateinit var to: EditText
    private var id: Int = 0
    private lateinit var button: Button
    private val model: Model by viewModels()
    private var isUpdate = false
    private lateinit var progress: ProgressDialog
    private lateinit var personViewModel: MyViewModel
    private lateinit var toUpdateOffline: Rule

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        personViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        name = findViewById(R.id.name)
        to = findViewById(R.id.to)
        from = findViewById(R.id.from)
        status = findViewById(R.id.status)
        level = findViewById(R.id.level)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isUpdate = true
            logd("update window")
            id = bundle.getInt("Id")
            putData(id)
            button.text = "Update"
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {
                val rule = Rule(
                    0,
                    name.text.toString(),
                    level.text.toString().toInt(),
                    status.text.toString(),
                    from.text.toString().toInt(),
                    to.text.toString().toInt(),
                    0
                )

                if (rule.name == "" || rule.name == " " || rule.status == " " || rule.status == "" ||
                    rule.level <= 0
                ) {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {
                            progress.show()

                            rule.id = id
                            val resp = model.update(rule)
                            if (resp == "off") {
                                toUpdateOffline = rule
                                showErrorMessage("The server is down.")
                            } else {
                                personViewModel.update(rule)
                                finish()
                            }
                            progress.dismiss()

                        } else {
                            progress.show()
                            val response = model.add(rule)
                            logd(" after saving $response")
                            if (response != "off") {
                                val toSave = deserialize(response)
                                if (toSave != null) {
                                    rule.id = toSave.id
                                    rule.status = toSave.status
                                    personViewModel.insert(rule)
                                    displayFinalMessage(rule)
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                } else {
                                    showErrorMessage("Trouble when saving.")
                                    progress.dismiss()
                                }
                            } else {
                                rule.changed = 1
                                personViewModel.insert(rule)
                                showErrorMessage("The server is off.")
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

    private fun putData(id: Int) {

        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val rule = model.getOne(id)
            logd("find one, with id $id: $rule")
            if (rule == null) {
                showErrorMessage("The rule is not valid.")
            } else {
                if (rule.id == -1) {
                    showErrorMessage("The server is down.")
                    button.isEnabled = false
                    finish()

                } else {
                    button.isEnabled = true
                    name.setText(rule.name)
                    level.setText(rule.level.toString())
                    status.setText(rule.status)
                    from.setText(rule.from.toString())
                    to.setText(rule.to.toString())
                }
            }

            progress.dismiss()

        }
    }

    private fun showErrorMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun deserialize(myString: String): Rule? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }

            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("from") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)
            return Rule(id, "", 12, status, 0, 0, 0)
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

                    val resp = model.update(toUpdateOffline)
                    if (resp == "off") {
                        showErrorMessage("The server is still down.")
                    } else {
                        personViewModel.update(toUpdateOffline)
                        finish()
                    }
                    progress.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun displayFinalMessage(rule: Rule) {
        val text = "The rule with the name ${rule.name} and level ${rule.level} has the" +
                " status ${rule.status} and from ${rule.from} to ${rule.to} "
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}