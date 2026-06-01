package com.example.coinminder.data.remote

import com.example.coinminder.data.model.CotacaoResponse
import retrofit2.http.GET

// Interface que descreve os endpoints que queremos consumir
interface CotacaoApiService {

    // GET https://economia.awesomeapi.com.br/json/last/USD-BRL,EUR-BRL
    @GET("json/last/USD-BRL,EUR-BRL")
    suspend fun getCotacoes(): CotacaoResponse  // suspend = executa em corrotina
}