package com.example.template.service
import com.google.gson.annotations.SerializedName

class VehicleCredentials(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name : String,
    @SerializedName("status")
    val status: String,
    @SerializedName("passengers")
    val passengers: Int,
    @SerializedName("driver")
    val driver: String,
    @SerializedName("paint")
    val paint: String,
    @SerializedName("capacity")
    val capacity: Int
)
