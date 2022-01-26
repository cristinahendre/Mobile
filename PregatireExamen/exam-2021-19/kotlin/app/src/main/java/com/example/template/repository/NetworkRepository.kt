package com.example.template.repository

import com.example.template.domain.Rule
import com.example.template.logd
import com.example.template.service.RuleCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Rule>? {
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

    suspend fun getAllLevel(level:Int): List<Rule>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getAllLevel(level)
            logd("[get all level] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun findOne(id: Int): Rule? {
        return try {

            val result = RestService.service?.findOne(id)
            logd("[get one network] $result")
            result
        } catch (e: Exception) {
            Rule(-1, "", -1, "", 0, 0, 0)
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


    suspend fun add(obj: Rule): String {
        try {
            logd("[add $obj network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    RuleCredentials(
                        obj.id,
                        obj.name, obj.level, obj.status, obj.from, obj.to
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(obj: Rule): String {
        try {
            logd("[update $obj network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    RuleCredentials(
                        obj.id,
                        obj.name, obj.level, obj.status, obj.from, obj.to
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}