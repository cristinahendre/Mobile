package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "dosare")
data class Dosar(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "nume")
    var nume: String,
    @ColumnInfo(name = "medie1")
    var medie1: Int,
    @ColumnInfo(name = "medie2")
    var medie2: Int,
    @ColumnInfo(name = "medie")
    var medie: Int,
    @ColumnInfo(name = "status")
    var status: Boolean,
    @ColumnInfo(name = "changed")
    var changed: Int
)
