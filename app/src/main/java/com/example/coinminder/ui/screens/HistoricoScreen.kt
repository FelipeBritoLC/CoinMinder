package com.example.coinminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coinminder.data.model.Despesa
import com.example.coinminder.viewmodel.DespesaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(navController: NavController, viewModel: DespesaViewModel) {
    val despesas by viewModel.despesasFiltradas.collectAsState()
    val textoBusca by viewModel.textoBusca.collectAsState()
    val categoriaFiltro by viewModel.categoriaFiltro.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Histórico") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // RF04 - Campo de Busca
            OutlinedTextField(
                value = textoBusca,
                onValueChange = { viewModel.onTextoBuscaChange(it) },
                placeholder = { Text("Buscar por título...") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                singleLine = true
            )

            // RF04 - Filtros de Categoria
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = (categoriaFiltro == null),
                    onClick = { viewModel.onCategoriaFiltroChange(null) },
                    label = { Text("Todas") }
                )
                listOf("Alimentação", "Transporte", "Lazer").forEach { cat ->
                    FilterChip(
                        selected = (categoriaFiltro == cat),
                        onClick = { viewModel.onCategoriaFiltroChange(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            // RF03 - Listagem em Tempo Real do Firestore
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(despesas) { despesa ->
                    DespesaItem(despesa)
                }
            }
        }
    }
}

@Composable
fun DespesaItem(despesa: Despesa) {
    // Faz a conversão matemática
    val valorConvertido = despesa.valor * despesa.cotacaoNoMomento
    // Formata para ficar bonitinho com 2 casas decimais (ex: 25.50)
    val valorFormatado = String.format("%.2f", valorConvertido)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Centraliza os itens verticalmente
        ) {
            Column {
                Text(despesa.titulo, fontWeight = FontWeight.Bold)
                Text(despesa.categoria, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                // Moeda original no topo
                Text(
                    text = "${despesa.moeda} ${despesa.valor}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // Valor convertido em R$ embaixo
                Text(
                    text = "R$ $valorFormatado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}