package com.example.examfeb.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.examfeb.dao.VehicleDao
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd

class VehicleRepository(private val vehicleDao: VehicleDao) {

    val allVehicles: LiveData<List<Vehicle>> = vehicleDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(veh: Vehicle) {
        logd("to insert $veh")
        vehicleDao.insert(veh)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Vehicle>) {
        logd("to insert all")
        vehicleDao.insertAll(all)

    }

    fun getVehiclesOrdered(): LiveData<List<Vehicle>> {
        return vehicleDao.getAllOrdered()
    }


    fun getVehiclesChanged(): LiveData<List<Vehicle>> {
        return vehicleDao.getVehiclesChanged()
    }

    suspend fun delete(id: Int) {
        logd("delete id in repo $id")
        vehicleDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("delete all in vehicle repository")
        vehicleDao.deleteAll()
    }

    suspend fun update(v: Vehicle) {
        logd("update vehicle")
        vehicleDao.update(v)
    }
}