package com.example.template

import android.annotation.SuppressLint
import android.util.Log
import com.example.template.domain.Rezervare
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*

@SuppressLint("StaticFieldLeak")
object WebSocketStuff {

    suspend fun start(nume:(rezervare:Rezervare) -> Unit) {

        val gson= Gson()
        val client = HttpClient {
            install(WebSockets)
        }

        client.webSocket(method = HttpMethod.Get, host = "10.0.2.2", port = 2025 ) {
            while (true) {
                val othersMessage = incoming.receive() as? Frame.Text ?: continue
                println(othersMessage.readText())
                val myMessage = readLine()
                Log.d("Razvi",othersMessage.readText())
                nume(gson.fromJson(othersMessage.readText(), Rezervare::class.java))
//
//                if (myMessage != null) {
//                    send(myMessage)
//                }
                //logd("\n\n\n Mesaj ${othersMessage.readText()}")
            }
        }

        client.close()
        println("Connection closed. Goodbye!")
    }
}