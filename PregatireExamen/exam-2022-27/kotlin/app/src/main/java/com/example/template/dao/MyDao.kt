package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Dosar

@Dao
interface MyDao {

    @Query("SELECT * FROM dosare where changed!=2 and status = 1 order by medie desc")
    fun getAll(): LiveData<List<Dosar>>

    @Query("SELECT * FROM dosare where changed!=2 and status = 0 order by medie desc")
    fun getNevalidate(): LiveData<List<Dosar>>

    @Query("SELECT * FROM dosare where changed != 0")
    fun getAllChanged(): LiveData<List<Dosar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Dosar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Dosar>)


    @Query("DELETE FROM dosare where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM dosare")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Dosar)
}