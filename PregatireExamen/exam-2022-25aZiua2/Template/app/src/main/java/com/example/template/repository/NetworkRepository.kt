package com.example.template.repository

import com.example.template.domain.Produs
import com.example.template.logd
import com.example.template.service.ProductCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Produs>? {
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


    suspend fun add(produs: Produs): String {
        try {
            logd("[add $produs network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    ProductCredentials(
                        produs.id, produs.nume, produs.tip,produs.cantitate,
                        produs.pret,produs.discount,produs.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(produs: Produs): String {
        try {
            logd("[update $produs network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    ProductCredentials(
                        produs.id, produs.nume, produs.tip,produs.cantitate,
                        produs.pret,produs.discount,produs.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}