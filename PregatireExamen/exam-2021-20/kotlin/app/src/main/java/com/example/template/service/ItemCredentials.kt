package com.example.template.service

import com.google.gson.annotations.SerializedName

class ItemCredentials(
    @SerializedName("id") val id:Int,
    @SerializedName("name") val name:String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Int,
    @SerializedName("status") val status: String,
)
