package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Dosar

@Dao
interface MyDao {

    @Query("SELECT * FROM dosars where changed!=2 and status =1 order by etaj,orientare,camera, nume")
    fun getAll(): LiveData<List<Dosar>>

    @Query("SELECT * FROM dosars where changed!=2 and status =0 order by etaj, medie desc ")
    fun getNeconfirmate(): LiveData<List<Dosar>>

    @Query("SELECT * FROM dosars where changed != 0")
    fun getAllChanged(): LiveData<List<Dosar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Dosar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Dosar>)


    @Query("DELETE FROM dosars where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM dosars")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Dosar)
}