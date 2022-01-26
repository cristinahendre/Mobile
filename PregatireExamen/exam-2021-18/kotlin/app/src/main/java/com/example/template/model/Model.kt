package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Exam
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Exam>>().apply { value = emptyList() }

    val data: LiveData<List<Exam>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Exam): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]")
        return myId

    }

    suspend fun join(entity:Exam): String {
        val resp = NetworkRepository.join(entity)
        logd("[model - join $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Exam>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }

    suspend fun getGroupsExams(name:String): List<Exam>? {
        val res = NetworkRepository.getGroupsExams(name)
        logd("[model-get groups exams] $name")
        return res
    }

    suspend fun getDraftExams(): List<Exam>? {
        val res = NetworkRepository.getDraftExams()
        logd("[model-get draft]")
        mutableData.value = res
        return res
    }

    suspend fun findOne(id:Int): Exam? {
        val res = NetworkRepository.getOne(id)
        logd("[model-get one] $res")
        return res
    }
}