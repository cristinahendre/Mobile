package com.example.roomversion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.example.roomversion.domain.Grade
import com.example.roomversion.models.Model
import com.example.roomversion.viewmodel.GradeViewModel
import com.example.roomversion.viewmodel.StudentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddGradeActivity : AppCompatActivity() {

    private lateinit var studentName: EditText
    private lateinit var grade: EditText
    private lateinit var date: EditText
    private lateinit var button: Button
    private val model: Model by viewModels()
    private var teacherId: Int = 0
    private var isUpdate = false
    private lateinit var gradeViewModel: GradeViewModel
    private lateinit var studentViewModel: StudentViewModel
    private var id: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_grade)
        gradeViewModel = ViewModelProviders.of(this).get(GradeViewModel::class.java)
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel::class.java)
        studentName = findViewById(R.id.student)
        grade = findViewById(R.id.grade)
        date = findViewById(R.id.date)
        button = findViewById(R.id.button_save)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt(ID, 0)
            if (id != 0) {
                isUpdate = true
                logd("update window")
                studentName.setText(bundle.getString(STUDENT_NAME))
                grade.setText(bundle.getString(GRADE))
                date.setText(bundle.getString(DATE))
                button.text = "Update"

            }
            teacherId = bundle.getInt(TEACHER_ID, 0)

        }


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener { view: View ->
            val replyIntent = Intent()
            var result = validate(
                studentName.text.toString(), date.text.toString(),
                grade.text.toString()
            )
            if (result == "") {
                GlobalScope.launch(Dispatchers.Main) {

                    val progressCircular = findViewById<ProgressBar>(R.id.progress_circular)
                    progressCircular.visibility = View.VISIBLE
                    view.animate()
                        .setDuration(4700)
                        .alpha(0.0F)
                        .withEndAction {

                            view.alpha = 1.0F
                            progressCircular.visibility = View.GONE
                        }
                    var myStudent = model.getStudentByName(studentName.text.toString())
                    if (myStudent != null) {
                        if (myStudent.id == -1 && myStudent.name == "") {
                            //the server is down
                            showErrorMessage("The server is down, using local data.")
                            myStudent =
                                studentViewModel.getStudentByName(studentName.text.toString())
                            if (myStudent == null) {
                                showErrorMessage("The student does not exist.")
                            }
                        }
                        val studId = myStudent.id
                        val myDate = LocalDate.parse(date.text.toString())
                        val note = Grade(
                            id,
                            teacherId,
                            studId,
                            grade.text.toString().toInt(),
                            myDate,
                            0
                        )
                        if (isUpdate) {
                            note.changed = 0
                            val resp = model.updateGrade(note)
                            if (resp == -1) {
                                showErrorMessage("The server is down.")
                                note.changed = 3
                            }
                            gradeViewModel.update(note)

                        } else {
                            val myId = model.addGrade(note)
                            logd("id after saving $myId")
                            if (myId != -1) {
                                note.id = myId
                                gradeViewModel.insert(note)
                            } else {
                                note.changed=1
                                gradeViewModel.insert(note)
                            }
                        }

                        result = ""
                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()

                    } else {
                        showErrorMessage("The student is invalid.")

                    }
                }

            } else {
                logd("Invalid student");
                showErrorMessage(result)

            }
            if (result != "") {
                showErrorMessage(result)
            }
        }
    }

    private fun showErrorMessage(myMessage: String) {
        this.runOnUiThread {
            Toast.makeText(
                this,
                myMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validate(student: String, date: String, grade: String): String {

        var errorMsg = ""
        if (student.isEmpty()) {
            errorMsg += " Invalid student name.\n"
        }
        if (date.isEmpty()) {
            errorMsg += " Empty date.\n"
        } else {
            try {
                var date = LocalDate.parse(date)
            } catch (e: Exception) {
                errorMsg += " Invalid date.\n"
            }
        }
        if (grade.isEmpty()) {
            errorMsg += " Empty grade.\n"
        } else {
            try {
                val gradeInt = grade.toInt()
                if (gradeInt < 0 || gradeInt > 10) {
                    errorMsg += " Invalid grade interval.\n"
                }
            } catch (e: Exception) {
                errorMsg += " Invalid grade."
            }
        }

        return errorMsg
    }

    companion object {
        const val STUDENT_NAME = "StudentName"
        const val ID = "Id"
        const val GRADE = "Grade"
        const val DATE = "Date"
        const val TEACHER_ID = "TeacherId"
    }
}

