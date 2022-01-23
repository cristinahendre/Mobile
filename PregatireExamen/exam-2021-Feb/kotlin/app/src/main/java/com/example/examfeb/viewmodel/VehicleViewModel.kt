package com.example.examfeb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.examfeb.database.VehicleDatabase
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd
import com.example.examfeb.repository.VehicleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VehicleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VehicleRepository
    var allVehicles: LiveData<List<Vehicle>>? = null
    var vehiclesChanged: LiveData<List<Vehicle>>? = null

    init {
        val vehicaleDao = VehicleDatabase.getDatabase(application, viewModelScope).vehicleDao()
        repository = VehicleRepository(vehicaleDao)
    }

    fun insertAll(gr: List<Vehicle>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
        repository.insertAll(gr)
        logd("inserted all")
    }


    fun insert(gr: Vehicle) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(gr)
        logd("insert in view model: $gr")
    }

    fun update(gr: Vehicle) = viewModelScope.launch(Dispatchers.IO) {

        repository.update(gr)
        logd("update in view model :$gr")
    }

    fun delete(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        logd("delete in view model")
        repository.delete(id)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        logd("delete all [mvvm]")
        repository.deleteAll()
    }

    fun getVehicles(){
        allVehicles=  repository.allVehicles
    }

    fun getVehiclesOrdered(){
        allVehicles=repository.getVehiclesOrdered()
    }



    fun getVehiclesChanged() {
        vehiclesChanged = repository.getVehiclesChanged()
        logd("[vehicle changed changed] ${vehiclesChanged!!.value}")
    }

}