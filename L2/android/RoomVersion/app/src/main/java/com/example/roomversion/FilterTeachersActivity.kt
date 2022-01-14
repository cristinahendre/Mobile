package com.example.roomversion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FilterTeachersActivity: AppCompatActivity() {
    companion object {
        const val STUDENT = "StudentName"
        const val GRADE = "Grade"
        const val DATE = "Date"
    }
    private var id = 0
    private lateinit var studentNameText: EditText
    private lateinit var dateText: EditText
    private lateinit var gradeText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_teachers)

        val fab = findViewById<Button>(R.id.btAdd)
        fab.setOnClickListener {
            studentNameText = findViewById(R.id.edtStudent)
            dateText = findViewById(R.id.edtDate)
            gradeText = findViewById(R.id.edtGrade)

//            if(studentNameText.text.isNullOrEmpty() && dateText.text.isNullOrEmpty() &&
//                    gradeText.text.isNullOrEmpty()){
//                Toast.makeText(baseContext, "Invalid data to filter.", Toast.LENGTH_SHORT).show()
//
//            }
//            else {

                val response = Intent()


                response.putExtra(GRADE, gradeText.text.toString())
                response.putExtra(DATE, dateText.text.toString())
                response.putExtra(STUDENT, studentNameText.text.toString())

                setResult(Activity.RESULT_OK, response)
                finish()
                logd("Finished in Filter for teachers activity.")
            }


    }


}