package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Produs
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Produs>>().apply { value = emptyList() }

    val data: LiveData<List<Produs>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Produs): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]")
        return myId

    }

    suspend fun update(entity: Produs): String {
        val resp = NetworkRepository.update(entity)
        logd("[model - update $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Produs>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }
}