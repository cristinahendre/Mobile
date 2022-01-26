package com.example.template.repository

import com.example.template.domain.Rezervare
import com.example.template.logd
import com.example.template.service.IdCredential
import com.example.template.service.RezervareCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Rezervare>? {
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


    suspend fun add(rezervare: Rezervare): String {
        try {
            logd("[add $rezervare network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    RezervareCredentials(
                        rezervare.id, rezervare.nume, rezervare.doctor, rezervare.data,
                        rezervare.ora, rezervare.detalii, rezervare.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun confirm(id: Int): String {
        try {
            logd("[confirm rezervare $id network]")
            if (RestService.service != null) {
                return RestService.service.confirm(
                  IdCredential(id)
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(rezervare: Rezervare): String {
        try {
            logd("[update $rezervare network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    RezervareCredentials(
                        rezervare.id, rezervare.nume, rezervare.doctor, rezervare.data,
                        rezervare.ora, rezervare.detalii, rezervare.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}