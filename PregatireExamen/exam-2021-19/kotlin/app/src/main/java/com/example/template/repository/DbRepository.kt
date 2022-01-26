package com.example.template.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.template.dao.MyDao
import com.example.template.domain.Rule
import com.example.template.logd

class DbRepository(private val myDao: MyDao) {

    var allData: LiveData<List<Rule>>? = null

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(p: Rule) {
        logd("[db repo] to insert $p")
        myDao.insert(p)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Rule>) {
        logd("[db repo] to insert all")
        myDao.insertAll(all)
    }


    fun getAllChanged(): LiveData<List<Rule>>{
        logd("[db repo] get data changed")
        return myDao.getAllChanged()
    }

    fun getAll(): LiveData<List<Rule>>{
        logd("[db repo] get data")

        return myDao.getAll()
    }


    suspend fun delete(id: Int) {
        logd("[db repo] delete id in repo, id= $id")
        myDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("[db repo] delete all in repo")
        myDao.deleteAll()
    }

    suspend fun update(p: Rule) {
        logd("[db repo] update $p")
        myDao.update(p)
    }
}