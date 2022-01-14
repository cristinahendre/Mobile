package com.example.roomversion.service
import com.example.roomversion.domain.Student
import com.google.gson.annotations.SerializedName

class LoginCredentials(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

class StudentTokenHolder(@SerializedName("student") val token: Student)