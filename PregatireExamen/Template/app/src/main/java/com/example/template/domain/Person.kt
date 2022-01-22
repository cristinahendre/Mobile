package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "people")
data class Person(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "age")
    var age: Int,

    @ColumnInfo(name = "changed")
    var changed: Int
)
