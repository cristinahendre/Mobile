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
import com.example.template.domain.Exam
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var group: EditText
    private lateinit var status: EditText
    private lateinit var details: EditText
    private lateinit var type: EditText
    private lateinit var students: EditText
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
        students = findViewById(R.id.students)
        type = findViewById(R.id.type)
        status = findViewById(R.id.status)
        details = findViewById(R.id.details)
        group = findViewById(R.id.group)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            isUpdate = true
            logd("update window")
            name.setText(bundle.getString("Name"))
            id = bundle.getInt("Id")
            students.setText(bundle.getInt("Students").toString())
            button.text = "Update"
        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            try {

                val exam = Exam(0, name.text.toString(),group.text.toString(),details.text.toString(),
                status.text.toString(),type.text.toString(),students.text.toString().toInt(),0)
                if (exam.name == "" || exam.name == " " || exam.group == "" || exam.group ==" " ||
                        exam.details == "" || exam.details == " " || exam.status== " " || exam.status=="" ||
                        exam.type == " " || exam.type == "") {
                    showErrorMessage("Invalid data.")
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isUpdate) {

                            progress.show()
                            exam.id = id
                            val resp = model.join(exam)
                            if (resp == "off") {
                                showErrorMessage("The server is down.")
                                exam.changed = 3
                            }
                            personViewModel.update(exam)
                            progress.dismiss()
                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()

                        } else {
                            progress.show()
                            val response = model.add(exam)
                            logd(" after saving $response")
                            if (response != "off") {
                                val myObj = deserialize(response)
                                if (myObj != null) {
                                    exam.id = myObj.id
                                    exam.status =myObj.status
                                    exam.students = myObj.students
                                    displayFinalMessage(exam)
                                    personViewModel.insert(exam)
                                    progress.dismiss()
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                }
                                else{
                                    showErrorMessage("Exam already exists!")
                                    progress.dismiss()
                                }
                            } else {
                                exam.changed = 1
                                personViewModel.insert(exam)
                                showErrorMessage("The server is off.")
                                progress.dismiss()
                                setResult(Activity.RESULT_OK, replyIntent)
                                finish()
                            }
                        }
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

    private fun displayFinalMessage(exam: Exam) {
        logd("display exam's $exam data")
        val text = "The exam with the name ${exam.name} and type ${exam.type }for the " +
                " group ${exam.group} contains the details ${exam.details} ; the status is " +
                " ${exam.status} and ${exam.students} are expected."
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Exam? {
// Exam(id=52, name=la, group=tt, details=f, status=draft, type=3a, students=0, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("type") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("students") + 9
            indexStop = myString.indexOf("changed") - 3
            val students = getStringFromArea(indexStart, indexStop, myString).toInt()
            return Exam(id,"","","",status,"",students,0)
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

