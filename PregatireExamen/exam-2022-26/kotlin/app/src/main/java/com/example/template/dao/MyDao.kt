package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Rezervare

@Dao
interface MyDao {

    @Query("SELECT * FROM rezervare where changed!=2  and status == 0 order by doctor, data, ora")
    fun getAll(): LiveData<List<Rezervare>>

    @Query("SELECT * FROM rezervare where changed != 0")
    fun getAllChanged(): LiveData<List<Rezervare>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Rezervare)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Rezervare>)


    @Query("DELETE FROM rezervare where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM rezervare")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Rezervare)
}