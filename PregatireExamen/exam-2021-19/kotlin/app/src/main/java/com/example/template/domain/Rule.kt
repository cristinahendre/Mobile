package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "rule")
data class Rule(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "level")
    var level: Int,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "from")
    var from: Int,
    @ColumnInfo(name = "to")
    var to: Int,
    @ColumnInfo(name = "changed")
    var changed: Int
)
