package com.example.roomversion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomversion.database.GradeDatabase
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.repository.StudentRepository
import com.example.roomversion.repository.TeacherRepository


class TeacherViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TeacherRepository

    init {
        val teacherDao = GradeDatabase.getDatabase(application,viewModelScope).teacherDao()
        repository = TeacherRepository(teacherDao )
    }

    suspend fun getTeacherById(id: Int): Teacher {
        return repository.getTeacherById(id);
    }

    suspend fun getTeacherByEmailPass(em:String, pass:String): Teacher  {
        return repository.getTeacherByEmailPass(em,pass);
    }

    suspend fun getTeacherByName(name: String): Teacher {
        return repository.getTeacherByName(name);
    }

    suspend fun getTeacherBySubjectName(name: String): Teacher {
        return repository.getTeacherBySubject(name);
    }


}