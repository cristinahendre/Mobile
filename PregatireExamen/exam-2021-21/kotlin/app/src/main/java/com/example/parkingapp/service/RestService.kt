package com.example.parkingapp.service

import android.database.Observable
import androidx.lifecycle.LiveData
import com.example.parkingapp.domain.Space
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

import java.util.concurrent.TimeUnit

object RestService {
    private const val URL = "http://10.0.2.2:2021/"

    interface Service {

        @GET("spaces")
        suspend fun getAll(): List<Space>


        @GET("free")
        suspend fun getFreeSpaces(): List<Space>

        @DELETE("space/{id}")
        suspend fun delete(@Path("id") id: Int): Response<Space>

        @POST("space")
        suspend fun add(
            @Body space: SpaceCredentials
        ): Response<Space>?

        @POST("take")
        suspend fun update(
            @Body vehicle: SpaceCredentials
        ): Response<Space>?

    }

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }


    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
        this.callTimeout(3, TimeUnit.SECONDS)
    }.build()


    private var gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val service: Service = retrofit.create(Service::class.java)

}