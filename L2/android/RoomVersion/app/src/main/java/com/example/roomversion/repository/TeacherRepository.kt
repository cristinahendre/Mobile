package com.example.roomversion.repository

import com.example.roomversion.dao.TeacherDao
import com.example.roomversion.domain.Teacher

class TeacherRepository(private val teacherDao: TeacherDao) {

    suspend fun getTeacherByEmailPass(em:String, pass:String): Teacher {
        return teacherDao.getTeacherByEmailPass(em, pass)
    }

    suspend fun getTeacherById(id:Int): Teacher {
       return teacherDao.getTeacherById(id)
    }

    suspend fun getTeacherByName(name:String): Teacher{
        return teacherDao.getTeacherByName(name)
    }

    suspend fun getTeacherBySubject(name:String): Teacher{
        return teacherDao.getTeacherBySubjectName(name)
    }
}