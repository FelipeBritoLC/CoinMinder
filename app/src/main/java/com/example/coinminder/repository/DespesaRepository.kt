package com.example.coinminder.repository

import com.example.coinminder.data.model.Despesa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DespesaRepository {

    // Referência à coleção "despesas" no Firestore
    private val colecao = FirebaseFirestore.getInstance().collection("despesas")

    // Salva um novo documento no Firestore e aguarda a conclusão
    suspend fun salvarDespesa(despesa: Despesa) {
        colecao.add(despesa).await()
    }

    // Retorna um Flow que emite a lista atualizada sempre que o Firestore mudar
    // callbackFlow + addSnapshotListener = escuta em tempo real (RF03)
    fun listarDespesas() = callbackFlow {
        val listener = colecao
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, erro ->
                if (erro != null || snapshot == null) return@addSnapshotListener

                val lista = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Despesa::class.java)?.copy(id = doc.id)
                }
                trySend(lista)  // emite a lista para quem estiver observando
            }

        // Cancela o listener quando o Flow for fechado (evita memory leak)
        awaitClose { listener.remove() }
    }
}