package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Rule

@Dao
interface MyDao {

    @Query("SELECT * FROM rule where changed!=2")
    fun getAll(): LiveData<List<Rule>>

    @Query("SELECT * FROM rule where changed != 0")
    fun getAllChanged(): LiveData<List<Rule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Rule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Rule>)


    @Query("DELETE FROM rule where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM rule")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Rule)
}