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


    suspend fun add(product: Produs): String {
        try {
            logd("[add $product network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    ProductCredentials(
                        product.id,
                        product.nume, product.tip, product.cantitate,product.pret,
                        product.discount,product.status,product.changed
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(product: Produs): String {
        try {
            logd("[update $product network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    ProductCredentials(
                        product.id,
                        product.nume, product.tip, product.cantitate,product.pret,
                        product.discount,product.status,product.changed
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}