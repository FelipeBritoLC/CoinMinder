package com.example.coinminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinminder.data.model.CotacaoResponse
import com.example.coinminder.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sealed class define os 3 estados possíveis da tela Home
sealed class CotacaoUiState {
    object Loading : CotacaoUiState()
    data class Success(val cotacao: CotacaoResponse) : CotacaoUiState()
    data class Error(val mensagem: String) : CotacaoUiState()
}

class CotacaoViewModel : ViewModel() {

    // _uiState é privado (mutável); uiState é público (somente leitura para a UI)
    private val _uiState = MutableStateFlow<CotacaoUiState>(CotacaoUiState.Loading)
    val uiState: StateFlow<CotacaoUiState> = _uiState

    init { buscarCotacoes() }  // Busca assim que o ViewModel é criado

    // Chama a API via Retrofit dentro de uma corrotina do viewModelScope
    fun buscarCotacoes() {
        viewModelScope.launch {
            _uiState.value = CotacaoUiState.Loading
            try {
                val resultado = RetrofitInstance.api.getCotacoes()
                _uiState.value = CotacaoUiState.Success(resultado)
            } catch (e: Exception) {
                _uiState.value = CotacaoUiState.Error("Falha na conexão: ${e.message}")
            }
        }
    }
}