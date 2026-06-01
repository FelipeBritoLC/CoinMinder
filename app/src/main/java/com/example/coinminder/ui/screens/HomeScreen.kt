package com.example.coinminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coinminder.viewmodel.CotacaoUiState
import com.example.coinminder.viewmodel.CotacaoViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: CotacaoViewModel) {
    // Escuta o estado da ViewModel de forma reativa
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CoinMinder", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Cotações em tempo real", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        // Exibe a UI baseada no estado da requisição Retrofit (RF01)
        when (val state = uiState) {
            is CotacaoUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CotacaoUiState.Error -> {
                Text(text = "Erro: ${state.mensagem}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.buscarCotacoes() }) { Text("Tentar Novamente") }
            }
            is CotacaoUiState.Success -> {
                // Cards Minimalistas de Cotação
                CardCotacao("Dólar (USD)", "R$ ${state.cotacao.usd.bid}")
                Spacer(modifier = Modifier.height(16.dp))
                CardCotacao("Euro (EUR)", "R$ ${state.cotacao.eur.bid}")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Botões de Navegação
        Button(
            onClick = { navController.navigate("nova_despesa") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Registrar Nova Despesa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("historico") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ver Histórico")
        }
    }
}

@Composable
fun CardCotacao(titulo: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(titulo, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}