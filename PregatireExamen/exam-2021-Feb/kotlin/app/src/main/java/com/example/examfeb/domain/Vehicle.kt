package com.example.examfeb.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "license")
    var license: String,
    @ColumnInfo(name = "status")
    var status:String,
    @ColumnInfo(name = "seats")
    var seats:  Int,
    @ColumnInfo(name = "driver")
    var driver:  String,
    @ColumnInfo(name = "color")
    var color:  String,
    @ColumnInfo(name = "cargo")
    var cargo:  Int,
    @ColumnInfo(name = "changed")
    var changed:Int

    //Changed = 0 (no changes), 1 (add), 2(delete), 3(update)


)