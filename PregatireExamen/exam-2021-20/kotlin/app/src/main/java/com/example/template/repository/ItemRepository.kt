package com.example.template.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.template.dao.ItemDao
import com.example.template.domain.Item
import com.example.template.logd

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(p: Item) {
        logd("to insert $p")
        itemDao.insert(p)
    }

    @WorkerThread
    suspend fun insertAll(all: List<Item>) {
        logd("to insert all")
        itemDao.insertAll(all)
    }


    fun getItemsChanged(): LiveData<List<Item>>{
        return itemDao.getItemsChanged()
    }


    fun getAllAvailable(): LiveData<List<Item>>{
        return itemDao.getAllAvailable()
    }


    suspend fun delete(id: Int) {
        logd("delete id in repo, id= $id")
        itemDao.delete(id)
    }

    suspend fun deleteAll() {
        logd("delete all in repo")
        itemDao.deleteAll()
    }

    suspend fun update(p: Item) {
        itemDao.update(p)
    }
}