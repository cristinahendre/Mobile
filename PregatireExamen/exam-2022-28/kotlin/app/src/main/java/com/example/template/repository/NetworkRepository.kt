package com.example.template.repository

import com.example.template.domain.Dosar
import com.example.template.logd
import com.example.template.service.DosarCredentials
import com.example.template.service.RestService
import com.example.template.service.SmallerCredentials

object NetworkRepository {

    suspend fun getAll(): List<Dosar>? {
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


    suspend fun add(dosar: Dosar): String {
        try {
            logd("[add $dosar network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    DosarCredentials(
                        dosar.id, dosar.nume,dosar.medie, dosar.etaj,dosar.orientare,
                        dosar.camera, dosar.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun validare(dosar: Dosar): String {
        try {
            logd("[confirm $dosar network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    SmallerCredentials(
                        dosar.id,dosar.etaj,
                        dosar.camera
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}