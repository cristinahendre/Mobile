package com.example.roomversion.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.roomversion.dao.GradeDao
import com.example.roomversion.domain.Grade
import com.example.roomversion.logd

class GradeRepository(private val gradeDao: GradeDao) {

    val allGrades: LiveData<List<Grade>> = gradeDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(grade: Grade) {
        logd("to insert $grade")
        gradeDao.insert(grade)
    }

    @WorkerThread
    suspend fun insertAll(grade: List<Grade>) {
        logd("to insert all")
        gradeDao.insertAll(grade)
        logd("i inserted all ")
    }

    fun getStudentsGrades(studId: Int): LiveData<List<Grade>>{
        return gradeDao.getAllStudentsGrades(studId)
    }

    fun getTeachersGradesChanged(studId: Int): LiveData<List<Grade>>{
        return gradeDao.getAllTeachersGradesChanged(studId)
    }

    fun getTeachersGrades(studId: Int): LiveData<List<Grade>>{
        return gradeDao.getAllTeachersGrades(studId)
    }

    suspend fun delete(id:Int){
        logd("delete id in repo $id")
        gradeDao.delete(id)
    }

    suspend fun deleteAll(){
        logd("delete all in grade repository")
        gradeDao.deleteAll()
    }

    suspend fun update(grade:Grade){
        gradeDao.update(grade)
    }
}