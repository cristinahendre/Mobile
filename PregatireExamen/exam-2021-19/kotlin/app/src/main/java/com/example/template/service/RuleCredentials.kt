package com.example.template.service

import com.google.gson.annotations.SerializedName

class RuleCredentials(

    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String,
    @SerializedName("level")
    val level: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("from")
    val from: Int,
    @SerializedName("to")
    val to: Int
)
