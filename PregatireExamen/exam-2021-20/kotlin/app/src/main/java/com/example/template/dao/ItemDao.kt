package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM items where changed!=2")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT * FROM items where (changed!=2) and (status == 'desired' or status == 'needed') order by price,quantity")
    fun getAllAvailable(): LiveData<List<Item>>

    @Query("SELECT * FROM items where changed != 0")
    fun getItemsChanged(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Item>)


    @Query("DELETE FROM items where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM items")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Item)
}