package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "products")
data class Produs(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "nume")
    var nume: String,
    @ColumnInfo(name = "tip")
    var tip: String,
    @ColumnInfo(name = "cantitate")
    var cantitate: Int,
    @ColumnInfo(name = "pret")
    var pret: Int,
    @ColumnInfo(name = "discount")
    var discount: Int,
    @ColumnInfo(name = "status")
    var status: Boolean,
    @ColumnInfo(name = "changed")
    var changed: Int
)
