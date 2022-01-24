package com.example.examfeb.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd
import com.example.examfeb.repository.NetworkRepository


class Model : ViewModel() {
    private val mutableVehicles = MutableLiveData<List<Vehicle>>().apply { value = emptyList() }

    val vehicles: LiveData<List<Vehicle>> = mutableVehicles


    suspend fun delete(id: Int): String {
        val response = NetworkRepository.delete(id)
        logd("[model- delete]")
        return response
    }

    suspend fun add(gr: Vehicle): String? {
        val myId = NetworkRepository.add(gr)
        logd("[model- add] $myId")
        return myId

    }

    suspend fun getVehicles(): List<Vehicle>? {
        val res = NetworkRepository.getAll()
        logd("[model-get all] $res")
        mutableVehicles.value = res
        return res
    }

    suspend fun getTenVehicles(): List<Vehicle>? {
        val res = NetworkRepository.getTenVehicles()
        logd("[model-get ten vehicles] ")
        return res
    }

    suspend fun getVehiclesByColor(color:String): List<Vehicle>? {
        val res = NetworkRepository.getVehiclesByColor(color)
        logd("[model-get vehicles by color] ")
        return res
    }

    suspend fun getDriversVehicles(color:String): List<Vehicle>? {
        val res = NetworkRepository.getDriversVehicles(color)
        logd("[model-get vehicles by driver] ")
        return res
    }

    suspend fun getColors(): List<String>? {
        val res = NetworkRepository.getColors()
        logd("[model-get colors] ")
        return res
    }

    suspend fun update(gr: Vehicle): Int {
        val resp = NetworkRepository.update(gr)
        logd("[model - update Vehicle] $resp")
        return resp
    }

}