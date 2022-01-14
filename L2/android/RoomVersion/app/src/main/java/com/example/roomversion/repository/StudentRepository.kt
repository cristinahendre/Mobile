package com.example.roomversion.repository

import com.example.roomversion.dao.StudentDao
import com.example.roomversion.dao.TeacherDao
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.logd


class StudentRepository(private val studentDao: StudentDao) {

    suspend fun getStudentByEmailPass(em:String, pass:String): Student {
       var  res = studentDao.getStudentByEmailPass(em, pass);
        logd("in repo, res "+res);
        return res;
    }

    suspend fun getStudentById(id:Int): Student {
        return studentDao.getStudentById(id);
    }

    suspend fun getStudentByName(name:String): Student {
        return studentDao.getStudentByName(name);
    }
}