package com.example.nudeclassification.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    val baseUrl = "http://192.168.43.217:8000/"

    val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(10,TimeUnit.MINUTES)
        .readTimeout(10,TimeUnit.MINUTES)
        .writeTimeout(10,TimeUnit.MINUTES)
        .build()
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient)
            .build()
    }
}