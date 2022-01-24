package com.example.examfeb.repository

import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd
import com.example.examfeb.service.RestService
import com.example.examfeb.service.VehicleCredentials

object NetworkRepository {


    suspend fun getAll(): List<Vehicle>? {
        return try {
            logd("network repo - get all")
            RestService.service.getAll()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTenVehicles(): List<Vehicle>? {
        return try {
            logd("network repo - get ten vehicles")
            RestService.service.getTenVehicles()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getVehiclesByColor(color: String): List<Vehicle>? {
        return try {
            logd("network repo - get vehicles by color $color ")
            val result = RestService.service.getVehiclesByColor(color)
            result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDriversVehicles(name: String): List<Vehicle>? {
        return try {
            logd("network repo - get vehicles belonging to driver $name ")
            val result = RestService.service.getDriversVehicles(name)
            result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getColors(): List<String>? {
        return try {
            logd("network repo -get colors ")
            val result = RestService.service.getColors()
            result
        } catch (e: Exception) {
            null
        }
    }

    suspend fun delete(id: Int): String {
        return try {
            logd("delete car with id $id")
            val msg = RestService.service.deleteVehicle(id)
            var r = ""
            r = if (msg.isSuccessful) {
                msg.body().toString()
            } else msg.message()
            r

        } catch (e: Exception) {
            "off"
        }
    }


    suspend fun add(vehicle: Vehicle): String? {
        return try {
            logd("add in network repository")
            val resp = RestService.service.add(
                VehicleCredentials(
                    vehicle.id, vehicle.license, vehicle.status,
                    vehicle.seats, vehicle.driver, vehicle.color, vehicle.cargo
                )
            )
            logd("resp is $resp ")
            var r = ""
            if (resp != null) {
                r = if (resp.isSuccessful) {
                    resp.body().toString()
                } else "Something went wrong."
            }
           r
        } catch (e: Exception) {
            logd("exception is $e")
            "off"
        }
    }

    suspend fun update(vehicle: Vehicle): Int {
        try {
            logd("[update]")
            if (RestService.service != null) {
                val msg = RestService.service.update(
                    VehicleCredentials(
                        vehicle.id, vehicle.license, vehicle.status,
                        vehicle.seats, vehicle.driver, vehicle.color, vehicle.cargo
                    )
                ).body()
                logd("message: $msg")
                if (msg == "OK") return 0
                return 1
            }
            return -1
        } catch (e: Exception) {
            return -1
        }
    }

}