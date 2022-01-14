package com.example.roomversion.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "grades")
data  class Grade(

    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    @ColumnInfo(name = "teacher_id")
    var teacherId:Int,

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    @ColumnInfo(name = "grade")
    var gradeValue:Int,

    @ColumnInfo(name = "date" ,defaultValue = "(strftime('%s','now','localtime'))")
    var date: LocalDate,

    @ColumnInfo(name = "changed")
    var changed:Int
)
