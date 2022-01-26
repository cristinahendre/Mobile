package com.example.template.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "exams")
data class Exam(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "group")
    var group: String,
    @ColumnInfo(name = "details")
    var details: String,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "students")
    var students: Int,
    @ColumnInfo(name = "changed")
    var changed: Int
)
