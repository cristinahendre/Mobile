package com.example.parkingapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parkingapp.domain.Space
import com.example.parkingapp.logd
import com.example.parkingapp.repository.NetworkRepository


class NetworkModel : ViewModel() {
    private val mutableSpaces = MutableLiveData<List<Space>>().apply { value = emptyList() }
    val spaces: LiveData<List<Space>> = mutableSpaces

    private val mutableFreeSpaces = MutableLiveData<List<Space>>().apply { value = emptyList() }
    val freeSpaces: LiveData<List<Space>> = mutableFreeSpaces


    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete]")
        return response
    }

    suspend fun add(gr: Space): String? {
        val myId = NetworkRepository.add(gr)
        logd("[model- add] $myId")
        return myId

    }

    suspend fun getAll(): List<Space>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all] $res")
        mutableSpaces.value = res
        return res
    }

    suspend fun getFreeSpaces(): List<Space>? {
        val res = NetworkRepository.getFreeSpaces()
        logd("[model-get free spaces] $res")
        mutableFreeSpaces.value = res
        return res
    }

    suspend fun update(gr: Space): String {
        val resp = NetworkRepository.update(gr)
        logd("[model - update Space] $resp")
        return resp
    }
}