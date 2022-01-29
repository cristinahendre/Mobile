package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Dosar
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Dosar>>().apply { value = emptyList() }

    val data: LiveData<List<Dosar>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Dosar): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]")
        return myId

    }

    suspend fun validare(entity: Dosar): String {
        val resp = NetworkRepository.validare(entity)
        logd("[model - confirm $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Dosar>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }
}