package com.example.examfeb.repository

import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd
import com.example.examfeb.service.RestService
import com.example.examfeb.service.VehicleCredentials

object NetworkRepository {


    suspend fun getAll(): List<Vehicle>? {
        try {
            logd("network repo ")
            val result = RestService.service.getAll()
            logd("[get all] $result")
            return result
        }
        catch (e:Exception){
            return null
        }
    }

    suspend fun delete(id:Int) : Int{
        //0=OK, -1 = Server down, 1= error
        try {
            if (RestService.service != null) {
                val msg = RestService.service.deleteVehicle(id)
                logd("[message after delete: ] $msg")
                if (msg.body() != "OK") {
                    return 1
                }
                return 0
            }
            return -1
        }
        catch (e: Exception){
            return -1
        }
    }


    suspend fun add(vehicle: Vehicle): String? {
        return try {
            logd("add in network repository")
            val resp = RestService.service.add(
                VehicleCredentials(vehicle.id,vehicle.license, vehicle.status,
                    vehicle.seats, vehicle.driver, vehicle.color, vehicle.cargo)
            )
            logd("resp is $resp")
            var r =""
            r = if(resp.isSuccessful){
                resp.body().toString()
            } else resp.message()
            r
        } catch (e: Exception){
            "off"
        }
    }

    suspend fun update(vehicle: Vehicle): Int {
        try {
            logd("[update]")
            if (RestService.service != null) {
                val msg = RestService.service.update(
                    VehicleCredentials(vehicle.id,vehicle.license, vehicle.status,
                        vehicle.seats, vehicle.driver, vehicle.color, vehicle.cargo)
                ).body()
                logd("message: $msg")
                if (msg == "OK") return 0
                return 1
            }
            return -1
        }
        catch (e:Exception){
            return  -1
        }
    }
    
}