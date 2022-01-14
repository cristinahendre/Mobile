package com.example.roomversion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.roomversion.database.GradeDatabase
import com.example.roomversion.domain.Grade
import com.example.roomversion.logd
import com.example.roomversion.repository.GradeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GradeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: GradeRepository
    var studentsGrades: LiveData<List<Grade>>? = null
    lateinit var studentsGradesFiltered: List<Grade>
    var teachersGrades: LiveData<List<Grade>>? = null
    var teachersGradesChanged:  LiveData<List<Grade>>? = null
    lateinit var teachersGradesFiltered: List<Grade>

    init {
        val gradeDao = GradeDatabase.getDatabase(application,viewModelScope).gradeDao()
        repository = GradeRepository(gradeDao)
    }

    fun insertAll(gr: List<Grade>) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteAll()
        repository.insertAll(gr)
        logd("inserted all")
    }


    fun insert(gr: Grade) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(gr)
        logd("insert in grade view model: $gr")
    }

    fun update(gr: Grade) = viewModelScope.launch(Dispatchers.IO){
        repository.update(gr)
    }

    fun delete(id:Int) = viewModelScope.launch(Dispatchers.IO){
        logd("delete[mvvm]")
        repository.delete(id)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO){
        logd("delete all [mvvm]")
        repository.deleteAll()
    }

    fun getStudentsGrades(id:Int){
        studentsGrades=  repository.getStudentsGrades(id)
    }

    fun getTeachersGrades(id:Int){
        teachersGrades=  repository.getTeachersGrades(id)
    }

    fun getTeachersGradesChanged(id:Int){
        teachersGradesChanged = repository.getTeachersGradesChanged(id)
        logd("[teachers grades changed] ${teachersGradesChanged!!.value}")
        logd("[teachers grades] ${teachersGrades!!.value}")
    }



    fun filterStudentsGrades(studId:Int, teacherId: String, date:String, gr:String){
        studentsGradesFiltered= studentsGrades?.value?.filter {
                grade ->  grade.studentId==studId &&
                (grade.teacherId.toString()== teacherId ||
                    grade.date.toString()== date || grade.gradeValue.toString() == gr)
        }!!
    }

    fun filterTeachersGrades(studId:String, teacherId: String, date:String, gr:String){
        teachersGradesFiltered= teachersGrades?.value?.filter {
                grade ->  grade.teacherId.toString() ==teacherId &&
                (grade.studentId.toString()== studId ||
                        grade.date.toString()== date || grade.gradeValue.toString() == gr)
        }!!
    }
}