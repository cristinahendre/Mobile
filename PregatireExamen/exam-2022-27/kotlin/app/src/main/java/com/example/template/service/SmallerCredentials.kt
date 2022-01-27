package com.example.template.service

import com.google.gson.annotations.SerializedName

class SmallerCredentials (

    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: Boolean
)