package com.example.template.service

import com.google.gson.annotations.SerializedName

class RezervareCredentials(

    @SerializedName("id")
    val id: Int,
    @SerializedName("nume")
    val nume: String,
    @SerializedName("doctor")
    val doctor: String,
    @SerializedName("data")
    val data: Int,
    @SerializedName("ora")
    val ora: Int,
    @SerializedName("detalii")
    val detalii: String,
    @SerializedName("status")
    val status: Boolean
)
