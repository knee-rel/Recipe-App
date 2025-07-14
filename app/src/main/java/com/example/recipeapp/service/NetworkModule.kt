package com.example.recipeapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}