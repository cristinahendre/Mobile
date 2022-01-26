package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "rezervare")
data class Rezervare(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "nume")
    var nume: String,
    @ColumnInfo(name = "doctor")
    var doctor: String,
    @ColumnInfo(name = "data")
    var data: Int,
    @ColumnInfo(name = "ora")
    var ora: Int,
    @ColumnInfo(name = "detalii")
    var detalii: String,
    @ColumnInfo(name = "status")
    var status: Boolean,
    @ColumnInfo(name = "changed")
    var changed: Int
)
