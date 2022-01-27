package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Vehicle

@Dao
interface MyDao {

    @Query("SELECT * FROM vehicles where changed!=2")
    fun getAll(): LiveData<List<Vehicle>>

    @Query("SELECT * FROM vehicles where changed != 0")
    fun getAllChanged(): LiveData<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Vehicle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Vehicle>)


    @Query("DELETE FROM vehicles where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Vehicle)
}