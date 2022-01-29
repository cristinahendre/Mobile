package com.example.template.service
import com.google.gson.annotations.SerializedName

class SmallerCredentials(

    @SerializedName(  "id")
    val id: Int ,
    @SerializedName("etaj")
    val etaj: Int,
    @SerializedName("camera")
    val camera: String
)
