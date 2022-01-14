package com.example.roomversion.service

import android.util.AndroidRuntimeException
import com.example.roomversion.logd
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.NullPointerException
import java.net.SocketTimeoutException
import kotlin.math.log

class TokenInterceptor : Interceptor {
    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response? {

        val original = chain.request()

        val originalUrl = original.url()
        val encodedPath = originalUrl.encodedPath()
        try {
            if (token == null || (original.method() == "post" && encodedPath.contains("/token-auth"))) {
                return chain.proceed(original)
            }
        } catch (e: SocketTimeoutException) {
            logd("socket timeout")
        }

        try {
            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", token!!)
                .url(originalUrl)

            val request = requestBuilder.build()

            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                handleErrorResponse(response)
                return Response.Builder().build()
            }
            return response
        } catch (e: NullPointerException) {
            logd("null")
        }
        return Response.Builder().build()
    }

    private fun handleErrorResponse(response: Response) {
        logd("errors")
    }
}