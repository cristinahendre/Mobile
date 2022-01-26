package com.example.template.repository

import com.example.template.domain.Exam
import com.example.template.logd
import com.example.template.service.ExamCredentials
import com.example.template.service.RestService

object NetworkRepository {


    suspend fun getDraftExams(): List<Exam>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getDraftExams()
            logd("[get all draft] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getAll(): List<Exam>? {
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

    suspend fun getGroupsExams(name:String): List<Exam>? {
        try {
            if (RestService.service == null) {
                return null
            }
            val result = RestService.service.getGroupsExams(name)
            logd("[get groups exams network] $result")
            return result
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getOne(id: Int): Exam? {
        return try {

            val result = RestService.service?.getOne(id)?.body()
            logd("[get one network ] $result")
            result
        } catch (e: Exception) {
            Exam(-1, "", "", "", "", "", 0, 0)
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


    suspend fun add(exam: Exam): String {
        try {
            logd("[add $exam network]")
            if (RestService.service != null) {
                return RestService.service.add(
                    ExamCredentials(
                        exam.id, exam.name, exam.group, exam.details, exam.status,
                        exam.type, exam.students
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }

    suspend fun join(exam: Exam): String {
        try {
            logd("[join $exam network]")
            if (RestService.service != null) {
                return RestService.service.join(
                    ExamCredentials(
                        exam.id, exam.name, exam.group, exam.details, exam.status,
                        exam.type, exam.students
                    )
                ).body().toString()
            }
            return "off"

        } catch (e: Exception) {
            return "off"
        }
    }
}