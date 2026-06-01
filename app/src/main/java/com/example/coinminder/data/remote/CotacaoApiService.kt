package com.example.coinminder.data.remote

import com.example.coinminder.data.model.CotacaoResponse
import retrofit2.http.GET

interface CotacaoApiService {
    @GET("json/last/USD-BRL,EUR-BRL")
    suspend fun getCotacoes(): CotacaoResponse
}