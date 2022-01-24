package com.example.parkingapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.parkingapp.dao.SpaceDao
import com.example.parkingapp.domain.Space
import com.example.parkingapp.logd


class SpaceRepository(private val spaceDao: SpaceDao) {

    val allSpaces: LiveData<List<Space>> = spaceDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(veh: Space) {
        logd("to insert $veh")
        spaceDao.insert(veh)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Space>) {
        logd("to insert all")
        spaceDao.insertAll(all)

    }
    suspend fun delete(id: Int) {
        logd("delete id in repo $id")
        spaceDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("delete all in Space repository")
        spaceDao.deleteAll()
    }

    suspend fun update(v: Space) {
        logd("update Space")
        spaceDao.update(v)
    }

    fun getAllChanged(): LiveData<List<Space>> {
        return spaceDao.getAllChanged()
    }


    fun getFreeSpaces(): LiveData<List<Space>> {
        return spaceDao.getFreeSpaces()
    }
}