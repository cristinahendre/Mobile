package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Item
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private var authToken: String? = null
    private val mutablePeople = MutableLiveData<List<Item>>().apply { value = emptyList() }

    val people: LiveData<List<Item>> = mutablePeople

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete]")
        return response
    }

    suspend fun add(gr: Item): String {
        val myId = NetworkRepository.add(gr)
        logd("[model- add]")
        return myId

    }

    suspend fun update(gr: Item): String {
        val resp = NetworkRepository.update(gr)
        logd("[model - update ] $resp")
        return resp
    }


    suspend fun getAll(): List<Item>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        mutablePeople.value = res
        return res
    }
}