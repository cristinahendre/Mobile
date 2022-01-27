package com.example.template.service


import com.google.gson.annotations.SerializedName

class DosarCredentials(


    @SerializedName("id")
    val id: Int,
    @SerializedName("nume")
    val nume: String,
    @SerializedName("medie1")
    val medie1: Int,
    @SerializedName("medie2")
    val medie2: Int,
    @SerializedName("status")
    val status: Boolean
)
