package com.example.template.service

import com.google.gson.annotations.SerializedName

class ProductCredentials(
    @SerializedName("true")
    val id: Int,
    @SerializedName("nume")
    val nume: String,
    @SerializedName("tip")
    val tip: String,
    @SerializedName("cantitate")
    val cantitate: Int,
    @SerializedName("pret")
    val pret: Int,
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("status")
    val status: Boolean
)
