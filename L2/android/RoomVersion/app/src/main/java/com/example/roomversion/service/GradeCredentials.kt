package com.example.roomversion.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import java.time.LocalDate

class GradeCredentials(
    @SerializedName("id") val id:Int,
    @SerializedName("studentId") val studId:Int,
    @SerializedName("teacherId") val teacherId: Int,
    @SerializedName("gradeValue") val grade: Int,
    @SerializedName("date") val  date: String,
    @SerializedName("changed") val  changed: Int
)
