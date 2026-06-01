package com.example.coinminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coinminder.viewmodel.DespesaViewModel
import com.example.coinminder.viewmodel.CotacaoViewModel
import com.example.coinminder.viewmodel.CotacaoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaDespesaScreen(navController: NavController, viewModel: DespesaViewModel, cotacaoViewModel: CotacaoViewModel) {
    // Variáveis de estado locais para os campos de texto
    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Alimentação") }
    var moeda by remember { mutableStateOf("USD") }
    val cotacaoState by cotacaoViewModel.uiState.collectAsState()

    val salvando by viewModel.salvando.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nova Despesa") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("O que você comprou?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor numérico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Categoria", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Simplificação: Botões de seleção em vez de Dropdown complexo
                listOf("Alimentação", "Transporte", "Lazer").forEach { cat ->
                    FilterChip(
                        selected = (categoria == cat),
                        onClick = { categoria = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Moeda Original", style = MaterialTheme.typography.labelLarge)
            Row {
                listOf("USD", "EUR").forEach { m ->
                    FilterChip(
                        selected = (moeda == m),
                        onClick = { moeda = m },
                        label = { Text(m) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botão Salvar envia pro Firestore (RF02)
            Button(
                onClick = {
                    val valorDouble = valor.toDoubleOrNull() ?: 0.0
                    if (titulo.isNotBlank() && valorDouble > 0) {

                        // Descobre a cotação da moeda escolhida naquele exato momento
                        var taxaAtual = 1.0
                        if (cotacaoState is CotacaoUiState.Success) {
                            val dadosApi = (cotacaoState as CotacaoUiState.Success).cotacao
                            taxaAtual = if (moeda == "USD") {
                                dadosApi.usd.bid.toDoubleOrNull() ?: 1.0
                            } else {
                                dadosApi.eur.bid.toDoubleOrNull() ?: 1.0
                            }
                        }

                        // Envia para o banco de dados com a taxa atual
                        viewModel.salvarDespesa(titulo, valorDouble, categoria, moeda, taxaAtual)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !salvando,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (salvando) "Salvando..." else "Salvar Despesa")
            }
        }
    }
}