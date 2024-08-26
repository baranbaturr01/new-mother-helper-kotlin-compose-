package com.baranbatur.newmotherhelper.components

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()
        val response = chain.proceed(request)
        val endTime = System.nanoTime()
        val duration = (endTime - startTime) / 1e6 // duration in milliseconds
        Log.d("HTTP1", "Request URL: ${request.url}")
        Log.d("HTTP1", "Response Code: ${response.code}")
        Log.d("HTTP1", "Duration: ${duration}ms")
        return response
    }
}