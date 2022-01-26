package com.example.template.service


import com.google.gson.annotations.SerializedName

class ExamCredentials(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String,
    @SerializedName("group")
    val group: String,
    @SerializedName("details")
    val details: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("students")
    val students: Int
)
