package com.example.template.service
import com.google.gson.annotations.SerializedName

class DosarCredentials(

    @SerializedName(  "id")
    val id: Int ,
    @SerializedName( "nume")
    val nume: String,
    @SerializedName( "medie")
    val medie: Int,
    @SerializedName("etaj")
    val etaj: Int,
    @SerializedName( "orientare")
    val orientare: Boolean,
    @SerializedName("camera")
    val camera: String,
    @SerializedName( "status")
    val status: Boolean
)
