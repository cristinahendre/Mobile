package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Rezervare
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Rezervare>>().apply { value = emptyList() }

    val data: LiveData<List<Rezervare>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Rezervare): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity] $myId")
        return myId

    }

    suspend fun confirm(id:Int): String {
        val myId = NetworkRepository.confirm(id)
        logd("[model- confirm $id] $myId")
        return myId

    }

    suspend fun update(entity: Rezervare): String {
        val resp = NetworkRepository.update(entity)
        logd("[model - update $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Rezervare>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }
}