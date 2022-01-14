package com.example.roomversion.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.logd
import com.example.roomversion.repository.NetworkRepository
import com.example.roomversion.service.LoginCredentials


class Model : ViewModel() {
    private var authToken: String? = null
    private val mutableStudentsGrades = MutableLiveData<List<Grade>>().apply { value = emptyList() }
    private val mutableTeachersGrades = MutableLiveData<List<Grade>>().apply { value = emptyList() }

    val studentsGrades: LiveData<List<Grade>> = mutableStudentsGrades
    val teachersGrades: LiveData<List<Grade>> = mutableTeachersGrades
    var teachersGradesFiltered : List<Grade>? = null


    fun filterStudentsGrades(
        studId: Int,
        teacherId: String,
        date: String,
        gr: String
    ): List<Grade>? {

        if (mutableStudentsGrades.value == null || mutableStudentsGrades.value?.isEmpty() == true) {
            return null
        }
        return mutableStudentsGrades.value?.filter { grade ->
            grade.studentId == studId &&
                    (grade.teacherId.toString() == teacherId ||
                            grade.date.toString() == date || grade.gradeValue.toString() == gr)
        }!!
    }

    fun filterTeachersGrades(
        studId: String,
        teacherId: String,
        date: String,
        gr: String
    ){
        teachersGradesFiltered = if (teachersGrades.value == null || teachersGrades.value?.isEmpty() == true) {
            null
        } else teachersGrades.value?.filter { grade ->
            grade.teacherId.toString() == teacherId &&
                    (grade.studentId.toString() == studId ||
                            grade.date.toString() == date || grade.gradeValue.toString() == gr)
        }!!
    }

    suspend fun authStudent(email: String, pass: String): Student? {
        if (authToken == null || authToken?.isEmpty() == true) {
            val student = NetworkRepository.authStudent(LoginCredentials(email, pass))
            logd("[model - auth student] $student")
            if (student != null) {
                return student
            }
        }
        return null
    }

    suspend fun authTeacher(email: String, pass: String): Teacher? {
        if (authToken == null || authToken?.isEmpty() == true) {
            val teacher = NetworkRepository.authTeacher(LoginCredentials(email, pass))
            logd("[model- auth teacher] $teacher")
            if (teacher != null) {
                return teacher
            }
        }
        return null
    }

    suspend fun deleteGrade(id: Int): Int {
        val response = NetworkRepository.deleteGrade(id)
        logd("[model- delete]")
        return response
    }

    suspend fun addGrade(gr: Grade): Int {
        val myId = NetworkRepository.addGrade(gr)
        logd("[model- add: new id = $myId")
        return myId

    }

    suspend fun updateGrade(gr: Grade): Int {
        val resp = NetworkRepository.updateGrade(gr)
        logd("[model - update grade] $resp")
        return resp
    }

    suspend fun getAllGrades(): List<Grade>? {
        val all = NetworkRepository.getAllGrades()
        logd("[model -get all grades:  $all]")
        return all
    }

    suspend fun getTeacherBySubject(sub: String): Teacher? {
        logd("[model - get teacher by subject]")
        return NetworkRepository.getTeacherBySubject(sub)
    }

    suspend fun getTeacherById(id: Int): Teacher? {
        logd("[model - get teacher by id]")
        return NetworkRepository.getTeacherById(id)
    }

    suspend fun getStudentById(id: Int): Student? {
        logd("[model - get student by id]")
        return NetworkRepository.getStudentById(id)
    }

    suspend fun getStudentByName(name: String): Student? {
        logd("[model - get student by name]")
        return NetworkRepository.getStudentByName(name)
    }


    suspend fun getStudentsGrades(id: Int): List<Grade>? {
        val res = NetworkRepository.getStudentsGrades(id)
        logd("[model-get students grades] $res")
        mutableStudentsGrades.value = res
        return res
    }

    suspend fun getTeachersGrades(id: Int): List<Grade>? {
        val res = NetworkRepository.getTeachersGrades(id)
        logd("[model-get teachers grades] $res")
        mutableTeachersGrades.value = res
        return res
    }
}