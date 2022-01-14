package com.example.roomversion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.roomversion.database.GradeDatabase
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.logd
import com.example.roomversion.repository.GradeRepository
import com.example.roomversion.repository.StudentRepository
import kotlinx.coroutines.*

class StudentViewModel(application: Application): AndroidViewModel(application) {
    private val repository: StudentRepository

    init {
        val studentDao = GradeDatabase.getDatabase(application,viewModelScope).studentDao()
        repository = StudentRepository(studentDao)
    }


    suspend fun getStudentById(id: Int): Student  {
       return repository.getStudentById(id);
    }

    suspend fun getStudentByName(name: String): Student{

        return repository.getStudentByName(name);

    }

     suspend fun getStudentByEmailPass(em:String,pass:String): Student{

        var student= repository.getStudentByEmailPass(em,pass);

         return student;

    }



}