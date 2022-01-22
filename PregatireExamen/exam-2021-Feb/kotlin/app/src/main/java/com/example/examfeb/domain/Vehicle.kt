package com.example.examfeb.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    var Id: Int,
    @ColumnInfo(name = "License")
    var License: String,
    @ColumnInfo(name = "Status")
    var Status:String,
    @ColumnInfo(name = "Seats")
    var Seats:  Int,
    @ColumnInfo(name = "Driver")
    var Driver:  String,
    @ColumnInfo(name = "Color")
    var Color:  String,
    @ColumnInfo(name = "Cargo")
    var Cargo:  Int,
    @ColumnInfo(name = "Changed")
    var Changed:Int

    //Changed = 0 (no changes), 1 (add), 2(delete), 3(update)


)