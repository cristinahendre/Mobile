package com.example.roomversion


import android.util.Log
import android.view.ViewGroup
import androidx.annotation.LayoutRes

private const val CACHE_TIME = 1000L * 60L * 60L // one hour

fun Any.logd(message: Any? = "no message!") {
    Log.d(this.javaClass.simpleName, message.toString())
}
fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = false): android.view.View {
    val inflater = android.view.LayoutInflater.from(context)
    return inflater.inflate(layoutId, this, attachToRoot)
}