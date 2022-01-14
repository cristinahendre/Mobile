package com.example.roomversion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FilterStudentsActivity: AppCompatActivity() {
    companion object {
        const val SUBJECT = "SubjectName"
        const val GRADE = "Grade"
        const val DATE = "Date"
    }
    private var id = 0
    private lateinit var subjectNameText: EditText
    private lateinit var dateText: EditText
    private lateinit var gradeText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_students)

        val fab = findViewById<Button>(R.id.btAdd)
        fab.setOnClickListener {
            subjectNameText = findViewById(R.id.edtSubject)
            dateText = findViewById(R.id.edtDate)
            gradeText = findViewById(R.id.edtGrade)

            val response = Intent()


            response.putExtra(GRADE, gradeText.text.toString())
            response.putExtra(DATE, dateText.text.toString())
            response.putExtra(SUBJECT, subjectNameText.text.toString())

            setResult(Activity.RESULT_OK, response)
            finish()
            logd("Finished in Filter for students activity.")
        }

    }


}