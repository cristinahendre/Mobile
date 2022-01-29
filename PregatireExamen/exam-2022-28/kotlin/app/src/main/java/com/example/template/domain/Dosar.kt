package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "dosars")
data class Dosar(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "nume")
    var nume: String,
    @ColumnInfo(name = "medie")
    var medie: Int,
    @ColumnInfo(name = "etaj")
    var etaj: Int,
    @ColumnInfo(name = "orientare")
    var orientare: Boolean,
    @ColumnInfo(name = "camera")
    var camera: String,
    @ColumnInfo(name = "status")
    var status: Boolean,
    @ColumnInfo(name = "changed")
    var changed: Int
)
