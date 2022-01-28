package com.example.template.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.template.domain.Produs

@Dao
interface MyDao {

    @Query("SELECT * FROM products where changed!=2")
    fun getAll(): LiveData<List<Produs>>

    @Query("SELECT * FROM products where changed != 0")
    fun getAllChanged(): LiveData<List<Produs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gr: Produs)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gr: List<Produs>)


    @Query("DELETE FROM products where id = :id ")
    suspend fun delete(id: Int)


    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Update
    suspend fun update(gr: Produs)
}