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
            logd("[get all ] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }


    suspend fun delete(id: Int): Int {

        //0=OK, -1 = Server down, 1= error
        try {
            logd("[delete]")
            if (RestService.service != null) {
                val msg = RestService.service.delete(id)
                if (msg.body() != "OK") {
                    return 1
                }
                return 0
            }
            return -1
        } catch (e: Exception) {
            return -1
        }
    }


    suspend fun add(person: Person): Int {
        try {
            logd("[ add ]")
            if (RestService.service != null) {
                return RestService.service.add(
                    PersonCredentials(
                        person.id,
                        person.name, person.age, person.changed
                    )
                ).body() ?: return -1
            }
            return -1
        } catch (e: Exception) {
            return -1
        }
    }

    suspend fun update(person: Person): Int {
        try {
            logd("[update]")
            if (RestService.service != null) {
                val msg = RestService.service.update(
                    PersonCredentials(
                        person.id,
                        person.name, person.age, person.changed
                    )
                ).body()
                if (msg == "OK") return 0
                return 1
            }
            return -1
        } catch (e: Exception) {
            return -1
        }
    }


}