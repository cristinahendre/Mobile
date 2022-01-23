package com.example.examfeb.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.examfeb.domain.Vehicle

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ")
    fun getAll(): LiveData<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( gr: Vehicle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( gr: List<Vehicle>)


    @Query("DELETE FROM vehicles where id = :id ")
    suspend fun delete(id:Int)

    @Query("SELECT * FROM vehicles where  changed != 0")
    fun getVehiclesChanged(): LiveData<List<Vehicle>>


    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()

    @Update
    suspend fun update( gr: Vehicle)
}