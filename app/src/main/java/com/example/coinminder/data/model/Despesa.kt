package com.example.coinminder.data.model

import com.google.firebase.Timestamp

// Representa um gasto de viagem — campos com default para o Firestore conseguir
// instanciar o objeto automaticamente via toObject()
data class Despesa(
    val id: String = "",
    val titulo: String = "",
    val valor: Double = 0.0,
    val categoria: String = "",   // Alimentação | Transporte | Hospedagem | Lazer
    val moeda: String = "",       // USD | EUR
    val cotacaoNoMomento: Double = 1.0,
    val dataCriacao: Timestamp? = null
)