package com.example.parkingapp.service

import com.google.gson.annotations.SerializedName

class SpaceCredentials(
    @SerializedName("id") val Id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("count")
    val count: Int
)
