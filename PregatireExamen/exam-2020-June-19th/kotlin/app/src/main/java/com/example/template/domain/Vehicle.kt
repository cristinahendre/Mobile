package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "passengers")
    var passengers: Int,
    @ColumnInfo(name = "driver")
    var driver: String,
    @ColumnInfo(name = "paint")
    var paint: String,
    @ColumnInfo(name = "capacity")
    var capacity: Int,
    @ColumnInfo(name = "changed")
    var changed: Int
)
