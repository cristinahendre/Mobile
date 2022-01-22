package com.example.examfeb.service

import com.google.gson.annotations.SerializedName

class VehicleCredentials(
    @SerializedName("Id") val Id: Int,
    @SerializedName("License")
    val License: String,
    @SerializedName("Status")
    val Status: String,
    @SerializedName("Seats")
    val Seats: Number,
    @SerializedName("Driver")
    val Driver: String,
    @SerializedName("Color")
    val Color: String,
    @SerializedName("Cargo")
    val Cargo: Number,
    @SerializedName("Changed")
    val Changed: Number
)
