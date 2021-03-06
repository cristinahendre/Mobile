package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Person
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Person>>().apply { value = emptyList() }

    val data: LiveData<List<Person>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Person): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]")
        return myId

    }

    suspend fun update(entity: Person): String {
        val resp = NetworkRepository.update(entity)
        logd("[model - update $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Person>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }
}