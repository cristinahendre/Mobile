package com.example.roomversion.service

import android.annotation.SuppressLint
import android.util.AndroidException
import android.util.AndroidRuntimeException
import com.example.roomversion.domain.DateDeserializer
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import com.example.roomversion.logd
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.lang.NullPointerException
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


object NetworkAPI {
    private const val URL = "http://10.0.2.2:8080/api/"

    interface Service {
        @GET("grades")
        suspend fun getAllGrades(): List<Grade>

        @GET("teacher/by/{id}")
        suspend fun getTeacherById(@Path("id") id: Int): Response<Teacher>

        @GET("student/by/{id}")
        suspend fun getStudentById(@Path("id") id: Int): Response<Student>

        @DELETE("{id}")
        suspend fun deleteGrade(@Path("id") id: Int): Response<String>

        @GET("student/{name}")
        suspend fun getStudentByName(@Path("name") name: String): Response<Student>

        @GET("teacher/subject/{subject}")
        suspend fun getTeacherBySubject(@Path("subject") subject: String): Response<Teacher>

        @GET("grades/student/{id}")
        suspend fun getStudentsGrades(@Path("id") id: Int): List<Grade>

        @GET("grades/teacher/{id}")
        suspend fun getTeachersGrades(@Path("id") id: Int): List<Grade>

        @POST("student")
        suspend fun authenticateStudent(@Body login: LoginCredentials): Response<Student>

        @POST("teacher")
        suspend fun authenticateTeacher(@Body login: LoginCredentials): Response<Teacher>

        @FormUrlEncoded
        @POST("grade")
        suspend fun addGrade(
            @Field("studId") studId: Int,
            @Field("teacherId") teacherId: Int,
            @Field("grade") grade: Int,
            @Field("date") date: LocalDate,
            @Field("changed") changed: Int
        ): Response<Int>


        @PUT("grade")
        suspend fun updateGrade(
            @Body grade: GradeCredentials
        ): Response<String>

    }

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

  //  val tokenInterceptor = TokenInterceptor()


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
                .addConverterFactory(GsonConverterFactory.create(gsonWithDate()))
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
                this.callTimeout(2, TimeUnit.SECONDS)
            //    this.addInterceptor(tokenInterceptor)


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

    private fun gsonWithDate2(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .registerTypeAdapter(LocalDate::class.java, DateDeserializer::class.java)
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    }


    private fun gsonWithDate(): Gson {
        val builder = GsonBuilder().setLenient()
        builder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<LocalDate?> {
            @SuppressLint("SimpleDateFormat")
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDate? {
                return try {
                    val timeStamp = json.asString.toLong()
                    return Instant.ofEpochMilli(timeStamp).atZone(ZoneId.systemDefault())
                        .toLocalDate()

                } catch (e: ParseException) {
                    e.printStackTrace()
                    null
                }
            }
        })
        builder.registerTypeAdapter(LocalDate::class.java, object : JsonDeserializer<LocalDate?> {
            @SuppressLint("SimpleDateFormat")
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDate? {
                return try {
                    LocalDate.parse(json.asString)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    null
                }
            }
        })
        return builder.create()
    }


}