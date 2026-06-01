package com.example.coinminder.repository

import com.example.coinminder.data.model.Despesa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DespesaRepository {

    private val colecao = FirebaseFirestore.getInstance().collection("despesas")


    suspend fun salvarDespesa(despesa: Despesa) {
        colecao.add(despesa).await()
    }

    fun listarDespesas() = callbackFlow {
        val listener = colecao
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null || snapshot == null) return@addSnapshotListener

                val lista = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Despesa::class.java)?.copy(id = doc.id)
                }
                trySend(lista)
            }


        awaitClose { listener.remove() }
    }
}