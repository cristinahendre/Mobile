package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Rule
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Rule>>().apply { value = emptyList() }

    val data: LiveData<List<Rule>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Rule): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]; response is $myId")
        return myId

    }

    suspend fun update(entity: Rule): String {
        val resp = NetworkRepository.update(entity)
        logd("[model - update $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Rule>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutableData.value = res
        return res
    }

    suspend fun getAllLevel(l:Int): List<Rule>? {
        val res = NetworkRepository.getAllLevel(l)
        logd("[model-get all level]")
        return res
    }

    suspend fun getOne(id:Int): Rule? {
        val res = NetworkRepository.findOne(id)
        logd("[model-get one]")
        return res
    }
}