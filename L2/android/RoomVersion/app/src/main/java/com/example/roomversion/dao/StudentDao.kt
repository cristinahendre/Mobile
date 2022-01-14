package com.example.roomversion.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.roomversion.domain.Student

@Dao
interface StudentDao {


    @Query("SELECT * FROM students  where id = :id ")
    suspend fun getStudentById(id:Int): Student


    @Query("SELECT * FROM students  where name = :name ")
    suspend fun getStudentByName(name:String): Student

    @Query("SELECT * FROM students  where email = :em and password =:pass ")
    suspend fun getStudentByEmailPass(em:String, pass:String): Student

}