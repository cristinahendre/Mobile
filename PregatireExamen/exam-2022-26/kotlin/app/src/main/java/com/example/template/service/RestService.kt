package com.example.template.service
import android.util.AndroidRuntimeException
import com.example.template.domain.Rezervare
import com.example.template.logd
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.NullPointerException

import java.util.concurrent.TimeUnit


object RestService {
    private const val URL = "http://10.0.2.2:2025/"

    interface Service {
        @GET("open")
        suspend fun getAll(): List<Rezervare>

        @DELETE("{id}")
        suspend fun delete(@Path("id") id: Int): Response<Rezervare>

        @POST("create")
        suspend fun add(
            @Body grade: RezervareCredentials): Response<Rezervare>

        @POST("confirm")
        suspend fun confirm(
            @Body id: IdCredential): Response<Rezervare>

        @PUT("person")
        suspend fun update(
            @Body grade: RezervareCredentials
        ): Response<Rezervare>

    }

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }



    private val tokenInterceptor =TokenInterceptor()
    private val client: OkHttpClient? = createClient()


    private var gson = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit = createRetrofit()

    val service: Service? = initService()

    private fun createRetrofit(): Retrofit? {
        try {
            if (client == null) return null;
            return Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        } catch (e: NullPointerException) {
            return null;
        } catch (e: AndroidRuntimeException) {
            logd("AndroidRuntimeException")
            return null
        }
    }

    private fun createClient(): OkHttpClient? {
        try {
            return OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
                this.callTimeout(3, TimeUnit.SECONDS)

            }.build()
        } catch (e: NullPointerException) {
            logd("NullPointerException")
            return null
        } catch (e: AndroidRuntimeException) {
            logd("AndroidRuntimeException")
            return null
        }

    }

    private fun initService(): Service? {
        try {
            if (retrofit != null) {
                return retrofit.create(Service::class.java)
            }
        } catch (e: NullPointerException) {
            return null
        } catch (e: AndroidRuntimeException) {
            logd("AndroidRuntimeException")
            return null
        }
        return null
    }


}