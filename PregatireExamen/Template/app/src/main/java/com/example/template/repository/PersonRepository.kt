package com.example.template.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.template.dao.PersonDao
import com.example.template.domain.Person
import com.example.template.logd

class PersonRepository(private val personDao: PersonDao) {

    val allPeople: LiveData<List<Person>> = personDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(p: Person) {
        logd("to insert $p")
        personDao.insert(p)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Person>) {
        logd("to insert all")
        personDao.insertAll(all)
    }


    fun getPeopleChanged(): LiveData<List<Person>>{
        return personDao.getAllPeopleChanged()
    }


    suspend fun delete(id: Int) {
        logd("delete id in repo, id= $id")
        personDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("delete all in repo")
        personDao.deleteAll()
    }

    suspend fun update(p: Person) {
        personDao.update(p)
    }
}