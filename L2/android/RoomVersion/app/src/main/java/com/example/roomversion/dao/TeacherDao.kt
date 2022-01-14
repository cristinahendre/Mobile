package com.example.roomversion.dao
import androidx.room.*
import com.example.roomversion.domain.Teacher

@Dao
interface TeacherDao {


    @Query("SELECT * FROM teachers  where id = :id ")
    suspend fun getTeacherById(id:Int): Teacher

    @Query("SELECT * FROM teachers  where name = :name ")
    suspend fun getTeacherByName(name:String): Teacher

    @Query("SELECT * FROM teachers  where subject = :name ")
    suspend fun getTeacherBySubjectName(name:String): Teacher

    @Query("SELECT * FROM teachers  where email = :em and password =:pass ")
    suspend fun getTeacherByEmailPass(em:String, pass:String): Teacher

}