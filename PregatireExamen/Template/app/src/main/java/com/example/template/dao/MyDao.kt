package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Person

@Dao
interface MyDao {

    @Query("SELECT * FROM people where changed!=2")
    fun getAll(): LiveData<List<Person>>

    @Query("SELECT * FROM people where changed != 0")
    fun getAllChanged(): LiveData<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Person>)


    @Query("DELETE FROM people where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM people")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Person)
}