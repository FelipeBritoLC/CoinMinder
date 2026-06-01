package com.example.coinminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinminder.data.model.Despesa
import com.example.coinminder.repository.DespesaRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DespesaViewModel : ViewModel() {

    private val repository = DespesaRepository()

    // ── Fontes de dados ─────────────────────────────────────────────
    private val _todasDespesas  = MutableStateFlow<List<Despesa>>(emptyList())
    private val _textoBusca     = MutableStateFlow("")
    private val _categoriaFiltro = MutableStateFlow<String?>(null)

    // ── Estados expostos à UI ────────────────────────────────────────
    val textoBusca:     StateFlow<String>  = _textoBusca
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro

    private val _salvando = MutableStateFlow(false)
    val salvando: StateFlow<Boolean> = _salvando

    // Lista filtrada: recalculada automaticamente quando qualquer fonte muda
    // combine() + stateIn() transforma 3 Flows em 1 StateFlow pronto para a UI
    val despesasFiltradas: StateFlow<List<Despesa>> =
        combine(_todasDespesas, _textoBusca, _categoriaFiltro) { lista, texto, categoria ->
            lista.filter { d ->
                val passaTexto     = d.titulo.contains(texto, ignoreCase = true)
                val passaCategoria = categoria == null || d.categoria == categoria
                passaTexto && passaCategoria
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init { listarDespesas() }

    // Inicia a escuta em tempo real do Firestore
    private fun listarDespesas() {
        viewModelScope.launch {
            repository.listarDespesas().collect { _todasDespesas.value = it }
        }
    }

    // Monta o objeto e persiste no Firestore
    fun salvarDespesa(titulo: String, valor: Double, categoria: String, moeda: String, cotacao: Double) {
        viewModelScope.launch {
            _salvando.value = true
            repository.salvarDespesa(
                Despesa(
                    titulo = titulo,
                    valor = valor,
                    categoria = categoria,
                    moeda = moeda,
                    cotacaoNoMomento = cotacao,
                    dataCriacao = Timestamp.now()
                )
            )
            _salvando.value = false
        }
    }

    // Atualiza os filtros (chamados pela UI a cada keystroke / clique)
    fun onTextoBuscaChange(novo: String)    { _textoBusca.value = novo }
    fun onCategoriaFiltroChange(cat: String?) { _categoriaFiltro.value = cat }
}