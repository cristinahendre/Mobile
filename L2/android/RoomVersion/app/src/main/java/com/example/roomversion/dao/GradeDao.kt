package com.example.roomversion.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.roomversion.domain.Grade

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades ")
    fun getAll(): LiveData<List<Grade>>

    @Query("SELECT * FROM grades where student_id = :id")
    fun getAllStudentsGrades(id:Int): LiveData<List<Grade>>

    @Query("SELECT * FROM grades where teacher_id = :id and changed != 2 ")
    fun getAllTeachersGrades(id:Int): LiveData<List<Grade>>

    @Query("SELECT * FROM grades where teacher_id = :id and changed != 0")
    fun getAllTeachersGradesChanged(id:Int): LiveData<List<Grade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( gr: Grade)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( gr: List<Grade>)


    @Query("DELETE FROM grades where id = :id ")
    suspend fun delete(id:Int)


    @Query("DELETE FROM grades")
    suspend fun deleteAll()

    @Update
    suspend fun update( gr: Grade)
}