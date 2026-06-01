package com.example.coinminder.data.model

import com.google.gson.annotations.SerializedName

// Representa os dados de UMA moeda retornados pela AwesomeAPI
data class Cotacao(
    val code: String = "",
    val bid: String  = ""   // "bid" = preço de compra (o valor que exibiremos)
)

// A API retorna um objeto com as chaves "USDBRL" e "EURBRL"
// @SerializedName mapeia o campo JSON para o nome que queremos em Kotlin
data class CotacaoResponse(
    @SerializedName("USDBRL") val usd: Cotacao,
    @SerializedName("EURBRL") val eur: Cotacao
)