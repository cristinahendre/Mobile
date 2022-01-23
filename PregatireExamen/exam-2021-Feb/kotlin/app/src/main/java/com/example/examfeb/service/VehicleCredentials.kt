package com.example.examfeb.service

import com.google.gson.annotations.SerializedName

class VehicleCredentials(
    @SerializedName("id") val Id: Int,
    @SerializedName("license")
    val License: String,
    @SerializedName("status")
    val Status: String,
    @SerializedName("seats")
    val Seats: Number,
    @SerializedName("driver")
    val Driver: String,
    @SerializedName("color")
    val Color: String,
    @SerializedName("cargo")
    val Cargo: Number
)
