package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Exam

@Dao
interface MyDao {

    @Query("SELECT * FROM exams where changed!=2")
    fun getAll(): LiveData<List<Exam>>

    @Query("SELECT * FROM exams where changed != 0")
    fun getAllChanged(): LiveData<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Exam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Exam>)


    @Query("DELETE FROM exams where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM exams")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Exam)
}