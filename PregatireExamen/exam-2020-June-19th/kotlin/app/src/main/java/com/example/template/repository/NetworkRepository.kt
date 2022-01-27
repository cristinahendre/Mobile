package com.example.template.repository

import com.example.template.domain.Vehicle
import com.example.template.logd
import com.example.template.service.VehicleCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Vehicle>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getAll()
            logd("[get all network] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getColors(): List<String>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getColors()
            logd("[get colors network] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getVehiclesOfColor(color:String): List<Vehicle>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getVehiclesOfColor(color)
            logd("[get vehicle for color $color network] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }


    suspend fun getDriversCars(driver:String): List<Vehicle>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getDriversCars(driver)
            logd("[get vehicles for driver $driver network] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }


    suspend fun delete(id: Int): String {

        //0=OK, -1 = Server down, 1= error
        try {
            logd("[delete $id network]")
            if (RestService.service != null) {
                val msg = RestService.service.delete(id)
                return msg.body().toString()
            }
            return "off"
        } catch (e: Exception) {
            return "off"
        }
    }


    suspend fun add(vehicle: Vehicle): String {
        try {
            logd("[add $vehicle network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    VehicleCredentials(
                        vehicle.id, vehicle.name, vehicle.status, vehicle.passengers,
                        vehicle.driver, vehicle.paint, vehicle.capacity
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(vehicle: Vehicle): String {
        try {
            logd("[update $vehicle network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    VehicleCredentials(
                        vehicle.id, vehicle.name, vehicle.status, vehicle.passengers,
                        vehicle.driver, vehicle.paint, vehicle.capacity
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}