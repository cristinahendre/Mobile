package com.example.parkingapp

import android.util.Log

fun Any.logd(message: Any? = "no message!") {
    Log.d(this.javaClass.simpleName, message.toString())
}