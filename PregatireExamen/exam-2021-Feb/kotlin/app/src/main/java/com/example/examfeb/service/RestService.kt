package com.example.examfeb.service

import android.util.AndroidRuntimeException
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.logd
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.NullPointerException
import java.time.LocalDate
import java.util.concurrent.TimeUnit

object RestService {
    private const val URL = "http://10.0.2.2:2021/"

    interface Service {

        @GET("review")
        suspend fun getAll(): List<Vehicle>

        @DELETE("{id}")
        suspend fun deleteVehicle(@Path("id") id: Int): Response<String>

        @POST("vehicle")
        suspend fun add(
            @Body vehicle: VehicleCredentials
        ): Response<Vehicle>

        @PUT("grade")
        suspend fun update(
            @Body vehicle: VehicleCredentials
        ): Response<String>

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