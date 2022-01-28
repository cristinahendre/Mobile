package com.example.template

import android.annotation.SuppressLint
import android.util.Log
import com.example.template.domain.Produs
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*

@SuppressLint("StaticFieldLeak")
object WebSocketStuff {

    suspend fun start(myFunction:(entity:Produs) -> Unit) {

        val gson= Gson()
        val client = HttpClient {
            install(WebSockets)
        }

        client.webSocket(method = HttpMethod.Get, host = "10.0.2.2", port = 2025 ) {
            while (true) {
                val othersMessage = incoming.receive() as? Frame.Text ?: continue
                println(othersMessage.readText())
                val myMessage = readLine()
                Log.d("MyTag",othersMessage.readText())
                myFunction(gson.fromJson(othersMessage.readText(), Produs::class.java))
            }
        }

        client.close()
        println("Connection closed. Goodbye!")
    }
}