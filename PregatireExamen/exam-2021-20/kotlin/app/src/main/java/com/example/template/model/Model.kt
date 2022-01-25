package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Item
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private var authToken: String? = null
    private val mutableLiveData = MutableLiveData<List<Item>>().apply { value = emptyList() }

    val liveData: LiveData<List<Item>> = mutableLiveData

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
        mutableLiveData.value = res
        return res
    }

    suspend fun getBoughtItems(): List<Item>? {
        val res = NetworkRepository.getBoughtItems()
        logd("[model-get bought items]")
        return res
    }
}