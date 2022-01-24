package com.example.parkingapp.repository

import androidx.lifecycle.LiveData
import com.example.parkingapp.domain.Space
import com.example.parkingapp.logd
import com.example.parkingapp.service.RestService
import com.example.parkingapp.service.SpaceCredentials
import com.google.gson.Gson


object NetworkRepository {

    suspend fun getAll(): List<Space>? {
        return try {
            logd("network repo - get all")
            RestService.service.getAll()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFreeSpaces():List<Space>?{
        return try {
            logd("network repo - get free spaces")
            RestService.service.getFreeSpaces()
        } catch (e: Exception) {
            null
        }
    }


    suspend fun delete(id: Int): String {
        val gson =Gson()
        return try {
            logd("delete with id $id")
            val msg = RestService.service.delete(id)
            var r = ""
            return gson.toJson(msg.body())
        } catch (e: Exception) {
            "off"
        }
    }


    suspend fun add(space: Space): String? {
        return try {
            logd("add in network repository")
            val resp = RestService.service.add(
                SpaceCredentials(space.id, space.number, space.status, space.address, space.count)
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

    suspend fun update(space: Space): String {
        return try {
            logd("update in network repository")
            val resp = RestService.service.update(
                SpaceCredentials(space.id, space.number, space.status, space.address, space.count)
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

}