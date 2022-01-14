package com.example.roomversion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.models.Model
import com.example.roomversion.viewmodel.GradeViewModel
import com.example.roomversion.viewmodel.StudentViewModel
import com.example.roomversion.viewmodel.TeacherViewModel
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity() {

    private val model: Model by viewModels()
    private lateinit var teacherViewModel: TeacherViewModel
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var gradeViewModel: GradeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        gradeViewModel = ViewModelProviders.of(this).get(GradeViewModel::class.java)
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel::class.java)
        teacherViewModel = ViewModelProviders.of(this).get(TeacherViewModel::class.java)
        val btLogin = findViewById<Button>(R.id.btLogin)

        //populateDB()
        btLogin.setOnClickListener { view: View ->
            logd("login")

            val progressCircular = findViewById<ProgressBar>(R.id.progress_circular)
            progressCircular.visibility = View.VISIBLE
            view.animate()
                .setDuration(11000)
                .alpha(0.0F)
                .withEndAction {

                    view.alpha = 1.0F
                    progressCircular.visibility = View.GONE
                }
            val email = findViewById<EditText>(R.id.email).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            if (email.contains("@elev")) {
                //it is a child

                logd("here is a child")

                var stud: Student?
                GlobalScope.launch(Dispatchers.Main) {

                    stud = model.authStudent(email, password)
                    logd("[login] stud = $stud")
                    if (stud == null) {
                      showErrorMessage("The student is invalid.")
                    } else {
                        if (stud!!.id == -1 && stud!!.name == "") {
                            showErrorMessage("The server is down.")
                            stud = studentViewModel.getStudentByEmailPass(email, password)
                            if (stud == null) {
                                showErrorMessage("The student does not exist.")
                            } else {
                                createStudentActivity(stud!!)
                            }
                        } else {
                            logd("student is $stud")
                            createStudentActivity(stud!!)
                        }
                    }
                }

            } else {
                if (email.contains("@prof")) {
                    //it is a teacher
                    logd("here is a teacher")

                    var teacher: Teacher?
                    GlobalScope.launch(Dispatchers.Main) {

                        teacher = model.authTeacher(email, password)
                        logd("[login] teacher = $teacher")
                        if (teacher == null) {
                           showErrorMessage("The teacher is invalid.")
                        } else {
                            if (teacher!!.id == -1 && teacher!!.name == "") {
                                //there is a network issue
                                showErrorMessage("The server is down.")
                                teacher = teacherViewModel.getTeacherByEmailPass(email, password)
                                logd("teacher in db $teacher")
                                if (teacher == null) {

                                  showErrorMessage("The teacher does not exist.")
                                } else {
                                    createTeacherActivity(teacher!!)
                                    progressCircular.visibility = View.GONE

                                }
                            } else {
                                createTeacherActivity(teacher!!)
                                progressCircular.visibility = View.GONE

                            }
                        }
                    }
                }

            }

        }
    }

    private fun createTeacherActivity(teacher: Teacher) {
        logd("my teacher is $teacher")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Name", teacher.name)
        intent.putExtra("Id", teacher.id)
        intent.putExtra("Subject", teacher.subject)
        logd("sending " + teacher.subject)
        startActivity(intent)
        finish()
    }


    private fun createStudentActivity(stud: Student) {
        logd("my student is $stud")
        val intent = Intent(this, StudentActivity::class.java)
        intent.putExtra("Name", stud.name)
        intent.putExtra("Id", stud.id)
        logd("sending " + stud.name)
        startActivity(intent)
        finish()

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


}