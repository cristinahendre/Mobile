package com.example.parkingapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parkingapp.domain.Space

@Dao
interface SpaceDao {
    @Query("SELECT * FROM spaces where changed != 2")
    fun getAll(): LiveData<List<Space>>

    @Query("SELECT * FROM spaces where changed != 2 and status == 'free' order by number")
    fun getFreeSpaces(): LiveData<List<Space>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Space)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Space>)


    @Query("DELETE FROM spaces where id = :id ")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM spaces where  changed != 0")
    fun getAllChanged(): LiveData<List<Space>>


    @Query("DELETE FROM spaces")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Space)
}