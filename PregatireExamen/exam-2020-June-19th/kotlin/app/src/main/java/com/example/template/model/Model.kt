package com.example.template.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.template.domain.Vehicle
import com.example.template.logd
import com.example.template.repository.NetworkRepository

class Model : ViewModel() {
    private val mutableData = MutableLiveData<List<Vehicle>>().apply { value = emptyList() }

    val data: LiveData<List<Vehicle>> = mutableData

    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete $id]")
        return response
    }

    suspend fun add(entity: Vehicle): String {
        val myId = NetworkRepository.add(entity)
        logd("[model- add $entity]")
        return myId

    }

    suspend fun update(entity: Vehicle): String {
        val resp = NetworkRepository.update(entity)
        logd("[model - update $entity ] $resp")
        return resp
    }


    suspend fun getAll(): List<Vehicle>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all]")
        return res
    }

    suspend fun getVehiclesOfColor(color:String): List<Vehicle>? {
        val res = NetworkRepository.getVehiclesOfColor(color)
        logd("[model-get vehicles of color] $res")
        mutableData.value = res
        return res
    }


    suspend fun getDriversCars(driver:String): List<Vehicle>? {
        val res = NetworkRepository.getDriversCars(driver)
        logd("[model-get vehicles for driver] $res")
        return res
    }

    suspend fun getColors(): List<String>? {
        val res = NetworkRepository.getColors()
        logd("[model-get colors]")
        return res
    }
}