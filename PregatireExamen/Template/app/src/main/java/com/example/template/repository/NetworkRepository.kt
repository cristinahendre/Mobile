package com.example.template.repository

import com.example.template.domain.Person
import com.example.template.logd
import com.example.template.service.PersonCredentials
import com.example.template.service.RestService

object NetworkRepository {

    suspend fun getAll(): List<Person>? {
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


    suspend fun add(person: Person): String {
        try {
            logd("[add $person network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    PersonCredentials(
                        person.id,
                        person.name, person.age, person.changed
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun update(person: Person): String {
        try {
            logd("[add $person network]")
            if (RestService.service != null) {
                return RestService.service.update(
                    PersonCredentials(
                        person.id,
                        person.name, person.age, person.changed
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }


}