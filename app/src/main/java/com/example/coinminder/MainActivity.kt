package com.example.coinminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coinminder.ui.screens.HomeScreen
import com.example.coinminder.ui.screens.HistoricoScreen
import com.example.coinminder.ui.screens.NovaDespesaScreen
import com.example.coinminder.ui.theme.CoinMinderTheme
import com.example.coinminder.viewmodel.CotacaoViewModel
import com.example.coinminder.viewmodel.DespesaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinMinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // O NavController gerencia as rotas (telas)
                    val navController = rememberNavController()

                    // Instanciamos as ViewModels aqui no topo para manter os dados
                    // vivos enquanto o usuário navega entre as telas
                    val cotacaoViewModel: CotacaoViewModel = viewModel()
                    val despesaViewModel: DespesaViewModel = viewModel()

                    // Definindo as 3 telas exigidas (RF05)
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController, cotacaoViewModel)
                        }
                        composable("nova_despesa") {
                            NovaDespesaScreen(navController, despesaViewModel, cotacaoViewModel)
                        }
                        composable("historico") {
                            HistoricoScreen(navController, despesaViewModel)
                        }
                    }
                }
            }
        }
    }
}