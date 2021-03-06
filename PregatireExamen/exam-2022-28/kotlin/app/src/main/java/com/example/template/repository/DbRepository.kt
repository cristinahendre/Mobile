package com.example.template.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.template.dao.MyDao
import com.example.template.domain.Dosar
import com.example.template.logd

class DbRepository(private val myDao: MyDao) {

    val allData: LiveData<List<Dosar>> = myDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(p: Dosar) {
        logd("[db repo] to insert $p")
        myDao.insert(p)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Dosar>) {
        logd("[db repo] to insert all")
        myDao.insertAll(all)
    }


    fun getAllChanged(): LiveData<List<Dosar>>{
        logd("[db repo] get data changed")
        return myDao.getAllChanged()
    }

    fun getNeconfirmate(): LiveData<List<Dosar>>{
        logd("[db repo] get neconfirmate")
        return myDao.getNeconfirmate()
    }


    suspend fun delete(id: Int) {
        logd("[db repo] delete id in repo, id= $id")
        myDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("[db repo] delete all in repo")
        myDao.deleteAll()
    }

    suspend fun update(p: Dosar) {
        logd("[db repo] update $p")
        myDao.update(p)
    }
}