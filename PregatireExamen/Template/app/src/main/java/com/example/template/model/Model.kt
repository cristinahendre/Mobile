package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Person
import com.example.template.logd
import com.example.template.repository.NetworkRepository
import com.example.template.service.RestService

class Model : ViewModel() {
    private var authToken: String? = null
    private val mutablePeople = MutableLiveData<List<Person>>().apply { value = emptyList() }

    val people: LiveData<List<Person>> = mutablePeople

    suspend fun delete(id: Int): Int {
        val response = NetworkRepository.delete(id)
        logd("[model- delete]")
        return response
    }

    suspend fun add(gr: Person): Int {
        val myId = NetworkRepository.add(gr)
        logd("[model- add: new id = $myId")
        return myId

    }

    suspend fun update(gr: Person): Int {
        val resp = NetworkRepository.update(gr)
        logd("[model - update ] $resp")
        return resp
    }


    suspend fun getAll(): List<Person>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all] $res")
        mutablePeople.value = res
        return res
    }
}