package com.example.coinminder.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton: criado uma só vez e reutilizado em todo o app
object RetrofitInstance {

    private const val BASE_URL = "https://economia.awesomeapi.com.br/"

    // "by lazy" = só instancia quando acessado pela primeira vez
    val api: CotacaoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // converte JSON → Kotlin
            .build()
            .create(CotacaoApiService::class.java)
    }
}