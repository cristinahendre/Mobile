package com.example.template.service

import com.google.gson.annotations.SerializedName

class PersonCredentials(
    @SerializedName("id") val id:Int,
    @SerializedName("name") val name:String,
    @SerializedName("age") val age: Int,
    @SerializedName("changed") val  changed: Int
)
