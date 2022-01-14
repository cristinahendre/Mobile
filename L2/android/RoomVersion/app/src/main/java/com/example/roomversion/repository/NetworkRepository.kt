package com.example.roomversion.repository

import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.logd
import com.example.roomversion.service.GradeCredentials
import com.example.roomversion.service.LoginCredentials
import com.example.roomversion.service.NetworkAPI


object NetworkRepository {


    suspend fun getAllGrades(): List<Grade>? {
        try {
            if (NetworkAPI.service == null) {
                return null
            }
            val result = NetworkAPI.service.getAllGrades()
            logd("[get all grades] $result")
            return result
        }
        catch (e:Exception){
            return null
        }
    }

    suspend fun getStudentByName(name:String): Student? {
        try {
            logd("[get student by name]")
            if (NetworkAPI.service == null) {
                return Student(-1, "", "", "")
            }
            return NetworkAPI.service.getStudentByName(name).body()
        }
        catch(e: Exception){
            return Student(-1, "", "", "")
        }
    }

    suspend fun getStudentById(id:Int): Student? {
        try {
            logd("[get student by id]")
            if (NetworkAPI.service == null) {
                return Student(-1, "", "", "")
            }
            return NetworkAPI.service.getStudentById(id).body()
        }
        catch (e:Exception){
            return Student(-1, "", "", "")
        }
    }

    suspend fun getTeacherById(id:Int): Teacher? {
        try {
            if (NetworkAPI.service == null) {
                return Teacher(-1, "", "", "", "")
            }
            val myTeacher = NetworkAPI.service.getTeacherById(id).body()
            logd("[get teacher by id] $myTeacher")
            return myTeacher
        }
        catch (e:Exception){
            return Teacher(-1, "", "", "", "")
        }
    }

    suspend fun getTeacherBySubject(sub:String): Teacher? {
        try {
            if (NetworkAPI.service == null) return Teacher(-1, "", "", "", "")
            val teacher = NetworkAPI.service.getTeacherBySubject(sub)
            logd("[get teacher by subject] $teacher")
            return teacher.body()
        }
        catch (e:Exception){
            return Teacher(-1, "", "", "", "")
        }
    }
    suspend fun getStudentsGrades(id: Int): List<Grade>? {
        try {
            if (NetworkAPI.service == null) {
                //the server if off
                return null
            }
            val result = NetworkAPI.service.getStudentsGrades(id)
            logd("[get students grades] $result")
            return result
        }
        catch (e: Exception){
            return null
        }
    }

    suspend fun getTeachersGrades(id: Int): List<Grade>? {
        try {
            if (NetworkAPI.service == null) {
                return null
            }
            val myTeachers = NetworkAPI.service.getTeachersGrades(id)
            logd("[get teachers grades] $myTeachers")
            return myTeachers
        }
        catch (e: Exception){
            return null
        }

    }

    suspend fun authStudent(credentials: LoginCredentials): Student? {
        return getStud(credentials)
    }

    suspend fun deleteGrade(id:Int) : Int{

        //0=OK, -1 = Server down, 1= error
        try {
            if (NetworkAPI.service != null) {
                val msg = NetworkAPI.service.deleteGrade(id)
                logd("[message after delete: ] $msg")
                if (msg.body() != "OK") {
                    return 1
                }
                return 0
            }
            return -1
        }
        catch (e: Exception){
            return -1
        }
    }

    suspend fun authTeacher(credentials: LoginCredentials): Teacher? {
        try {
            logd("[auth teacher]")
            if (NetworkAPI.service != null) {
                return NetworkAPI.service.authenticateTeacher(credentials).body()
            }
            return Teacher(-1, "", "", "", "")
        }
        catch(e:java.lang.Exception){
            return Teacher(-1, "", "", "", "")
        }
    }

    suspend fun addGrade(grade: Grade): Int {
        try {
            if (NetworkAPI.service != null) {
                val id = NetworkAPI.service.addGrade(
                    grade.studentId, grade.teacherId,
                    grade.gradeValue, grade.date, grade.changed
                ).body()
                logd("[add grade] new id = $id")
                if (id == null) return -1
                return id
            }
            return -1
        }
        catch (e: Exception){
            return -1
        }
    }

    suspend fun updateGrade(grade: Grade): Int {
        try {
            logd("[update]")
            if (NetworkAPI.service != null) {
                val msg = NetworkAPI.service.updateGrade(
                    GradeCredentials(
                        grade.id,
                        grade.studentId, grade.teacherId,
                        grade.gradeValue, grade.date.toString(), grade.changed
                    )
                ).body()
                logd("message: $msg")
                if (msg == "OK") return 0
                return 1
            }
            return -1
        }
        catch (e:Exception){
            return  -1
        }
    }


    private suspend fun getStud(credentials: LoginCredentials): Student? {
        try {
            logd("[get student by data]")
            if (NetworkAPI.service != null)
                return NetworkAPI.service.authenticateStudent(credentials).body()
            return Student(-1, "", "", "")
        }
        catch (e:Exception){
            return Student(-1, "", "", "")
        }

    }

//    fun setToken(authToken: String?) {
//        NetworkAPI.tokenInterceptor.token = authToken
//    }
}