package com.example.template.repository

import com.example.template.domain.Item
import com.example.template.logd
import com.example.template.service.ItemCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Item>? {
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

    suspend fun getBoughtItems(): List<Item>? {
        return try {

            val result = RestService.service?.getBoughtItems()
            logd("[get bought network] $result")
            result
        } catch (e: Exception) {
            null
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


    suspend fun add(item: Item): String {
        try {
            logd("[add $item network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    ItemCredentials(
                        item.id,
                        item.name, item.quantity, item.price, item.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(item: Item): String {
        try {
            logd("[add $item network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    ItemCredentials(
                        item.id,
                        item.name, item.quantity, item.price, item.status
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }


}