# 💰 CoinMinder

> **Aplicativo Android para controle de gastos de viagem com conversão de moeda em tempo real.**

CoinMinder permite registrar despesas feitas em dólar (USD) ou euro (EUR) durante uma viagem, convertendo automaticamente os valores para reais (BRL) com a cotação vigente no momento do gasto. Os dados são sincronizados em tempo real com o Firebase Firestore, e as cotações vêm direto da AwesomeAPI.

---

## 📋 Índice

1. [Funcionalidades](#-funcionalidades)
2. [Tecnologias Utilizadas](#-tecnologias-utilizadas)
3. [Arquitetura do Projeto](#-arquitetura-do-projeto)
4. [Estrutura de Pastas](#-estrutura-de-pastas)
5. [Pré-requisitos](#-pré-requisitos)
6. [Configuração do Firebase](#-configuração-do-firebase)
7. [Como Executar](#-como-executar)
8. [Telas do Aplicativo](#-telas-do-aplicativo)
9. [Como o Código Funciona — Explicação Detalhada](#-como-o-código-funciona--explicação-detalhada)
   - [Camada de Dados (data)](#camada-de-dados-data)
   - [Camada de Repositório (repository)](#camada-de-repositório-repository)
   - [Camada de ViewModel](#camada-de-viewmodel)
   - [Camada de UI (ui/screens)](#camada-de-ui-uiscreens)
10. [Fluxo Completo de Dados](#-fluxo-completo-de-dados)
11. [API de Cotações](#-api-de-cotações)
12. [Dependências](#-dependências-completas)

---

## ✅ Funcionalidades

| Código | Funcionalidade                                                                 |
|--------|--------------------------------------------------------------------------------|
| RF01   | Exibir cotações em tempo real de USD/BRL e EUR/BRL na tela inicial             |
| RF02   | Registrar uma nova despesa com título, valor, categoria e moeda de origem      |
| RF03   | Listar despesas em tempo real (sincronização automática via Firestore)          |
| RF04   | Filtrar despesas por texto (busca no título) e por categoria                   |
| RF05   | Navegar entre 3 telas: Home, Nova Despesa e Histórico                         |

---

## 🛠 Tecnologias Utilizadas

| Tecnologia                   | Para que serve no projeto                                              |
|------------------------------|------------------------------------------------------------------------|
| **Kotlin**                   | Linguagem principal do Android moderno                                 |
| **Jetpack Compose**          | Framework declarativo para construção da interface gráfica             |
| **Material Design 3**        | Sistema de design do Google — botões, cards, tipografia, cores         |
| **Navigation Compose**       | Gerencia a navegação entre telas sem precisar de Fragments             |
| **Firebase Firestore**       | Banco de dados NoSQL na nuvem com sincronização em tempo real          |
| **Retrofit 2**               | Biblioteca para fazer chamadas HTTP/REST de forma simples              |
| **Gson**                     | Converte JSON (texto) em objetos Kotlin automaticamente                |
| **Kotlin Coroutines**        | Permite executar tarefas assíncronas (rede, banco) sem travar a tela   |
| **StateFlow / Flow**         | Fluxo reativo de dados: a UI se atualiza automaticamente quando muda   |
| **ViewModel (Lifecycle)**    | Mantém os dados vivos durante rotação de tela e navegação              |
| **AwesomeAPI**               | API pública e gratuita de cotações de moedas brasileiras               |
| **Gradle (Kotlin DSL)**      | Sistema de build do projeto, configurado em `.kts`                     |

---

## 🏗 Arquitetura do Projeto

O projeto segue a arquitetura **MVVM (Model-View-ViewModel)** recomendada pelo Google para Android. Ela separa claramente três responsabilidades:

```
┌─────────────────────────────────────────────────────────┐
│                        UI (View)                        │
│   HomeScreen / NovaDespesaScreen / HistoricoScreen      │
│   → Exibe dados. Não sabe de onde vêm.                  │
└──────────────────────┬──────────────────────────────────┘
                       │ observa StateFlow
┌──────────────────────▼──────────────────────────────────┐
│                    ViewModel                            │
│   CotacaoViewModel / DespesaViewModel                   │
│   → Processa lógica. Expõe estados para a UI.           │
└──────────────────────┬──────────────────────────────────┘
                       │ chama funções
┌──────────────────────▼──────────────────────────────────┐
│               Repository / Data Sources                 │
│   DespesaRepository + RetrofitInstance + Firestore      │
│   → Busca e salva dados (rede e banco).                 │
└─────────────────────────────────────────────────────────┘
```

**Por que MVVM?**

- A UI **nunca** acessa diretamente o banco de dados ou a rede.
- Se o usuário girar a tela, o ViewModel sobrevive — os dados não são perdidos.
- Cada camada tem uma única responsabilidade (princípio SOLID).

---

## 📁 Estrutura de Pastas

```
app/src/main/java/com/example/coinminder/
│
├── MainActivity.kt               ← Ponto de entrada. Configura navegação e temas.
│
├── data/
│   ├── model/
│   │   ├── CotacaoResponse.kt    ← Representa a resposta JSON da API de cotações
│   │   └── Despesa.kt            ← Representa um gasto registrado pelo usuário
│   └── remote/
│       ├── CotacaoApiService.kt  ← Define o endpoint HTTP da AwesomeAPI
│       └── RetrofitInstance.kt   ← Singleton: cria o cliente HTTP uma única vez
│
├── repository/
│   └── DespesaRepository.kt      ← Salva e lista despesas no Firestore
│
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt         ← Tela inicial: cotações + botões de navegação
│   │   ├── NovaDespesaScreen.kt  ← Formulário para registrar nova despesa
│   │   └── HistoricoScreen.kt    ← Lista de despesas com busca e filtro
│   └── theme/
│       ├── Color.kt              ← Paleta de cores do app
│       └── Theme.kt              ← Aplica o tema Material3 globalmente
│
└── viewmodel/
    ├── CotacaoViewModel.kt       ← Busca cotações da API e expõe o estado para a UI
    └── DespesaViewModel.kt       ← Gerencia lista de despesas, filtros e salvamento
```

---

## 📦 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

| Ferramenta                 | Versão mínima | Download                                      |
|----------------------------|---------------|-----------------------------------------------|
| Android Studio             | Hedgehog+     | https://developer.android.com/studio          |
| JDK (Java Development Kit) | 11            | Incluído no Android Studio                    |
| Android SDK                | API 26+       | Instalável pelo Android Studio SDK Manager    |
| Conta Google               | —             | Necessária para criar projeto no Firebase      |

> **Nota sobre Kotlin:** Você não precisa instalar Kotlin separadamente. O Android Studio já inclui o compilador Kotlin. A linguagem é 100% compatível com Java, mas mais moderna e concisa.

---

## 🔥 Configuração do Firebase

O CoinMinder usa o Firebase Firestore para persistir as despesas. Siga os passos abaixo:

### 1. Criar projeto no Firebase

1. Acesse [https://console.firebase.google.com](https://console.firebase.google.com)
2. Clique em **"Adicionar projeto"** e dê um nome (ex: `coinminder`)
3. Desative o Google Analytics (opcional) e clique em **"Criar projeto"**

### 2. Registrar o aplicativo Android

1. Na tela do projeto, clique no ícone **Android** (`</>`)
2. Em **"Nome do pacote Android"**, digite exatamente: `com.example.coinminder`
3. Clique em **"Registrar aplicativo"**
4. Faça o download do arquivo **`google-services.json`**
5. **Mova esse arquivo** para a pasta `app/` do projeto (mesmo nível que `build.gradle.kts`)

```
coinminder/
├── app/
│   ├── google-services.json  ← AQUI ✅
│   ├── build.gradle.kts
│   └── src/
```

### 3. Ativar o Firestore

1. No console do Firebase, vá em **"Firestore Database"** no menu lateral
2. Clique em **"Criar banco de dados"**
3. Escolha **"Iniciar no modo de teste"** (libera leitura/escrita por 30 dias)
4. Selecione a região mais próxima (ex: `southamerica-east1`) e clique em **"Concluído"**

> **Atenção:** O modo de teste é adequado para desenvolvimento. Antes de publicar, configure as regras de segurança do Firestore.

---

## ▶️ Como Executar

### Via Android Studio (recomendado)

```bash
# 1. Clone o repositório
git clone https://github.com/FelipeBritoLC/CoinMinder.git
cd coinminder

# 2. Abra no Android Studio
# File → Open → selecione a pasta coinminder

# 3. Aguarde o Gradle sincronizar (pode levar alguns minutos na primeira vez)

# 4. Conecte um dispositivo Android ou inicie um emulador
# Tools → Device Manager → Create Device

# 5. Clique no botão ▶ (Run) ou pressione Shift+F10
```

### Via linha de comando

```bash
# Certifique-se que o ANDROID_HOME está configurado no PATH

# Build e instalação em dispositivo conectado
./gradlew installDebug

# Apenas build (gera o APK em app/build/outputs/apk/debug/)
./gradlew assembleDebug
```

### Requisitos do dispositivo/emulador

- **API mínima:** 26 (Android 8.0 Oreo)
- **Arquitetura:** ARM ou x86
- **Permissão de Internet:** já declarada no `AndroidManifest.xml`

---

## 📱 Telas do Aplicativo

### 🏠 HomeScreen — Tela Inicial

**Rota de navegação:** `"home"` (tela de entrada)

**O que o usuário vê:**
- Nome do app "CoinMinder" em destaque
- Card com a cotação atual do **Dólar (USD → BRL)**
- Card com a cotação atual do **Euro (EUR → BRL)**
- Indicador de carregamento enquanto a API responde
- Mensagem de erro com botão "Tentar Novamente" se a rede falhar
- Botão **"Registrar Nova Despesa"** → navega para NovaDespesaScreen
- Botão **"Ver Histórico"** → navega para HistoricoScreen

---

### 📝 NovaDespesaScreen — Registrar Despesa

**Rota de navegação:** `"nova_despesa"`

**Campos do formulário:**

| Campo         | Tipo          | Descrição                                             |
|---------------|---------------|-------------------------------------------------------|
| Título        | Texto livre   | O que foi comprado (ex: "Jantar em restaurante")      |
| Valor         | Numérico      | Quanto foi gasto na moeda estrangeira                 |
| Categoria     | Seleção única | Alimentação / Transporte / Lazer                      |
| Moeda         | Seleção única | USD (Dólar) ou EUR (Euro)                             |

**O que acontece ao salvar:**
1. O app captura a cotação atual da moeda escolhida (já carregada na HomeScreen)
2. Monta um objeto `Despesa` com todos os campos + a cotação do momento
3. Salva no Firestore via `DespesaRepository.salvarDespesa()`
4. Retorna automaticamente para a tela anterior

---

### 📊 HistoricoScreen — Histórico de Despesas

**Rota de navegação:** `"historico"`

**O que o usuário vê:**
- Campo de busca por texto (filtra pelo título da despesa)
- Filtros de categoria: **Todas / Alimentação / Transporte / Lazer**
- Lista rolável de cards, cada um exibindo:
  - Título e categoria da despesa
  - Valor na moeda original (ex: `USD 45.00`)
  - Valor convertido em reais (ex: `R$ 225.50`)

**A lista é atualizada automaticamente** — se outro dispositivo adicionar uma despesa no mesmo Firestore, ela aparece instantaneamente sem recarregar.

---

## 🔬 Como o Código Funciona — Explicação Detalhada

### Camada de Dados (data)

#### `Despesa.kt` — O modelo de uma despesa

```kotlin
data class Despesa(
    val id: String = "",
    val titulo: String = "",
    val valor: Double = 0.0,
    val categoria: String = "",
    val moeda: String = "",
    val cotacaoNoMomento: Double = 1.0,
    val dataCriacao: Timestamp? = null
)
```

Uma `data class` em Kotlin é um tipo especial de classe criado apenas para guardar dados. O compilador gera automaticamente funções como `equals()`, `toString()` e `copy()`.

Todos os campos têm **valores padrão** (`= ""`). Isso é obrigatório para o Firestore: quando ele lê um documento do banco, usa reflexão para criar o objeto — sem construtores sem parâmetros, o app quebraria.

O campo `cotacaoNoMomento` registra a cotação *no instante do gasto*, não a cotação atual. Assim, o histórico permanece correto mesmo que o câmbio mude depois.

---

#### `CotacaoResponse.kt` — O modelo da resposta da API

```kotlin
data class Cotacao(
    val code: String = "",
    val bid: String  = ""
)

data class CotacaoResponse(
    @SerializedName("USDBRL") val usd: Cotacao,
    @SerializedName("EURBRL") val eur: Cotacao
)
```

A AwesomeAPI retorna um JSON com as chaves `"USDBRL"` e `"EURBRL"`. A anotação `@SerializedName` instrui o Gson a mapear essas chaves para os campos `usd` e `eur` em Kotlin, que têm nomes mais legíveis.

O campo `bid` (preço de compra) contém a cotação como texto (`String`). Quando precisamos fazer cálculos, convertemos com `.toDoubleOrNull()`.

---

#### `CotacaoApiService.kt` — O contrato HTTP

```kotlin
interface CotacaoApiService {
    @GET("json/last/USD-BRL,EUR-BRL")
    suspend fun getCotacoes(): CotacaoResponse
}
```

Uma `interface` em Kotlin define um contrato — o que pode ser feito, sem implementar como. O Retrofit lê essa interface e cria automaticamente a implementação que faz a chamada HTTP.

`@GET(...)` diz que é uma requisição do tipo GET e indica o caminho do endpoint.

`suspend` marca a função como uma **corrotina** — ela pode ser pausada enquanto aguarda a resposta da rede, sem bloquear a thread principal (o que causaria o app travar).

---

#### `RetrofitInstance.kt` — O cliente HTTP singleton

```kotlin
object RetrofitInstance {
    private const val BASE_URL = "https://economia.awesomeapi.com.br/"

    val api: CotacaoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CotacaoApiService::class.java)
    }
}
```

`object` em Kotlin cria um **singleton** — uma única instância que existe enquanto o app está rodando. Isso evita criar múltiplos clientes HTTP desnecessariamente.

`by lazy` significa que o objeto só é criado na **primeira vez** que `RetrofitInstance.api` for acessado. Nas acessos seguintes, a instância já existente é reutilizada.

O `GsonConverterFactory` é o "tradutor": converte o JSON da resposta HTTP em objetos Kotlin automaticamente.

---

### Camada de Repositório (repository)

#### `DespesaRepository.kt` — O intermediário com o Firestore

```kotlin
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
```

**`colecao`** aponta para o nó `"despesas"` no Firestore. Cada documento dentro dessa coleção é uma despesa.

**`salvarDespesa`** chama `.add(despesa)` — o Firestore converte o objeto Kotlin em JSON e cria um documento com ID gerado automaticamente. O `.await()` é uma extensão das corrotinas que "pausa" a função até o Firestore confirmar a gravação.

**`listarDespesas`** usa `callbackFlow` — um padrão que converte APIs baseadas em callback (o padrão do Firestore) em um `Flow` do Kotlin. O `addSnapshotListener` é chamado pelo Firestore sempre que há mudança nos dados — pode ser uma inserção, edição ou exclusão. A cada chamada, a lista completa é remontada com `mapNotNull` (que ignora documentos malformados) e emitida via `trySend`. O `awaitClose` garante que o listener é removido quando o Flow é cancelado, evitando vazamento de memória.

---

### Camada de ViewModel

#### `CotacaoViewModel.kt` — Gerencia o estado das cotações

```kotlin
sealed class CotacaoUiState {
    object Loading : CotacaoUiState()
    data class Success(val cotacao: CotacaoResponse) : CotacaoUiState()
    data class Error(val mensagem: String) : CotacaoUiState()
}

class CotacaoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CotacaoUiState>(CotacaoUiState.Loading)
    val uiState: StateFlow<CotacaoUiState> = _uiState

    init { buscarCotacoes() }

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
```

**`sealed class CotacaoUiState`** é uma classe selada — um conjunto fechado e seguro de estados possíveis. A tela sempre está em exatamente um dos três estados: `Loading`, `Success` ou `Error`. Isso força o código a tratar todos os casos no `when`.

**`MutableStateFlow`** é como uma "caixa" que guarda o estado atual e notifica qualquer observador quando o valor muda. O prefixo `_` (underscore) é convenção: o `_uiState` interno pode ser modificado, mas o `uiState` público é somente leitura para a UI.

**`viewModelScope.launch`** inicia uma corrotina vinculada ao ciclo de vida do ViewModel — quando o ViewModel for destruído, a corrotina é cancelada automaticamente.

**`init { buscarCotacoes() }`** garante que a busca começa assim que o ViewModel é criado, sem precisar que a UI chame explicitamente.

---

#### `DespesaViewModel.kt` — Gerencia lista, filtros e salvamento

```kotlin
class DespesaViewModel : ViewModel() {
    private val _todasDespesas   = MutableStateFlow<List<Despesa>>(emptyList())
    private val _textoBusca      = MutableStateFlow("")
    private val _categoriaFiltro = MutableStateFlow<String?>(null)

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
}
```

O ponto central aqui é o `combine`. Ele observa **três Flows simultaneamente**: a lista completa vinda do Firestore, o texto de busca e o filtro de categoria. Sempre que **qualquer um** dos três mudar, o `combine` recalcula automaticamente a lista filtrada.

`stateIn` converte o Flow resultante em um `StateFlow` com um valor inicial (`emptyList()`) para que a UI tenha algo para mostrar imediatamente.

`SharingStarted.WhileSubscribed(5_000)` significa: mantenha o Flow ativo enquanto houver pelo menos um observador. Após 5 segundos sem observadores (ex: tela em segundo plano), o Flow é pausado para economizar recursos.

---

### Camada de UI (ui/screens)

As telas são escritas em **Jetpack Compose**, um framework declarativo. Em vez de criar arquivos XML de layout separados, você descreve a interface diretamente em Kotlin usando funções chamadas **Composables** (marcadas com `@Composable`).

A ideia principal: você diz **"o que mostrar"** baseado no estado atual, e o Compose decide **como redesenhar** apenas as partes que mudaram.

#### `HomeScreen.kt`

```kotlin
@Composable
fun HomeScreen(navController: NavController, viewModel: CotacaoViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // ...
    when (val state = uiState) {
        is CotacaoUiState.Loading -> CircularProgressIndicator()
        is CotacaoUiState.Error   -> Text("Erro: ${state.mensagem}")
        is CotacaoUiState.Success -> {
            CardCotacao("Dólar (USD)", "R$ ${state.cotacao.usd.bid}")
            CardCotacao("Euro (EUR)",  "R$ ${state.cotacao.eur.bid}")
        }
    }
}
```

`collectAsState()` transforma o `StateFlow` do ViewModel em um estado que o Compose entende. Quando o valor do StateFlow muda, o Compose recompõe automaticamente apenas os composables afetados.

O `when` em Kotlin funciona como um `switch` mais poderoso e seguro. Como `CotacaoUiState` é uma sealed class, o compilador garante que todos os casos foram tratados.

---

#### `HistoricoScreen.kt` — A lista com filtros

```kotlin
LazyColumn {
    items(despesas) { despesa ->
        DespesaItem(despesa)
    }
}
```

`LazyColumn` é o equivalente Compose de uma `RecyclerView`. O prefixo "Lazy" indica que ele só renderiza os itens visíveis na tela — se houver 1000 despesas, apenas as 10 visíveis são criadas, economizando memória.

#### Como a conversão é calculada em `DespesaItem`

```kotlin
val valorConvertido = despesa.valor * despesa.cotacaoNoMomento
val valorFormatado = String.format("%.2f", valorConvertido)
```

Multiplicamos o valor original pela cotação **salva no momento do gasto**. O `String.format("%.2f", ...)` formata o número com exatamente 2 casas decimais (ex: `225.50`).

---

## 🔄 Fluxo Completo de Dados

### Fluxo 1: Buscar Cotações

```
App inicia
    └→ MainActivity cria CotacaoViewModel
         └→ init { buscarCotacoes() }
              └→ viewModelScope.launch { ... }  ← corrotina inicia
                   └→ RetrofitInstance.api.getCotacoes()  ← HTTP GET
                        └→ AwesomeAPI responde com JSON
                             └→ Gson converte JSON → CotacaoResponse
                                  └→ _uiState.value = Success(resultado)
                                       └→ HomeScreen recompõe automaticamente
                                            └→ Exibe os cards com cotações
```

### Fluxo 2: Salvar Despesa

```
Usuário preenche o formulário e clica "Salvar"
    └→ NovaDespesaScreen captura a cotação atual do CotacaoViewModel
         └→ viewModel.salvarDespesa(titulo, valor, categoria, moeda, taxa)
              └→ DespesaViewModel.salvarDespesa()
                   └→ Monta objeto Despesa com Timestamp.now()
                        └→ DespesaRepository.salvarDespesa(despesa)
                             └→ colecao.add(despesa).await()  ← Firestore
                                  └→ navController.popBackStack()  ← volta à tela anterior
```

### Fluxo 3: Listar e Filtrar Despesas

```
HistoricoScreen é aberta
    └→ DespesaViewModel.listarDespesas() já rodando desde o init
         └→ DespesaRepository.listarDespesas() → callbackFlow
              └→ addSnapshotListener registrado no Firestore
                   └→ Firestore emite lista a cada mudança
                        └→ _todasDespesas.value = lista
                             └→ combine() recalcula despesasFiltradas
                                  └→ HistoricoScreen recompõe a LazyColumn

Usuário digita no campo de busca
    └→ viewModel.onTextoBuscaChange("jantar")
         └→ _textoBusca.value = "jantar"
              └→ combine() é disparado novamente
                   └→ lista filtrada só mostra despesas cujo título contém "jantar"
                        └→ LazyColumn atualiza instantaneamente
```

---

## 🌐 API de Cotações

**AwesomeAPI** — [https://economia.awesomeapi.com.br](https://economia.awesomeapi.com.br)

**Endpoint usado:**
```
GET https://economia.awesomeapi.com.br/json/last/USD-BRL,EUR-BRL
```

**Exemplo de resposta JSON:**
```json
{
  "USDBRL": {
    "code": "USD",
    "codein": "BRL",
    "name": "Dólar Americano/Real Brasileiro",
    "bid": "5.1234",
    "ask": "5.1290",
    "timestamp": "1717200000"
  },
  "EURBRL": {
    "code": "EUR",
    "codein": "BRL",
    "name": "Euro/Real Brasileiro",
    "bid": "5.5678",
    "ask": "5.5750",
    "timestamp": "1717200000"
  }
}
```

O campo **`bid`** é o preço de compra da moeda — o valor que o app exibe e usa para conversão.

A API é **pública, gratuita e não requer autenticação**.

---

## 📦 Dependências Completas

### `app/build.gradle.kts`

```kotlin
// Interface gráfica
androidx.compose:compose-bom:2025.03.00          // Gerencia versões de todas as libs Compose
androidx.compose.ui:ui                            // Componentes base do Compose
androidx.compose.ui:ui-tooling-preview            // Preview no Android Studio
androidx.compose.material3:material3              // Componentes visuais Material Design 3
androidx.compose.ui:ui-tooling (debug)            // Inspeção de layout em modo debug

// Core Android
androidx.core:core-ktx:1.15.0                    // Extensões Kotlin para APIs Android
androidx.activity:activity-compose:1.10.1        // Integra Compose com ComponentActivity

// Ciclo de vida e ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7  // viewModel() dentro de Composables
androidx.lifecycle:lifecycle-runtime-ktx:2.8.7         // Extensões de corrotinas para Lifecycle

// Navegação entre telas
androidx.navigation:navigation-compose:2.8.9      // NavHost, NavController, composable()

// Firebase
com.google.firebase:firebase-bom:34.14.0          // Gerencia versões de todas as libs Firebase
com.google.firebase:firebase-firestore             // SDK do Cloud Firestore

// Rede (HTTP)
com.squareup.retrofit2:retrofit:2.11.0            // Cliente HTTP para Android
com.squareup.retrofit2:converter-gson:2.11.0      // Conversor JSON ↔ Kotlin via Gson

// Corrotinas (assincronicidade)
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0          // Suporte a corrotinas no Android
org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0    // .await() para Tasks do Firebase
```

### `build.gradle.kts` (Projeto)

```kotlin
com.android.application:8.7.0                    // Plugin de build de apps Android
org.jetbrains.kotlin.android:2.0.21              // Plugin Kotlin para Android
org.jetbrains.kotlin.plugin.compose:2.0.21       // Plugin do compilador do Compose
com.google.gms.google-services:4.4.4             // Plugin que lê o google-services.json
```

### Versões de SDK

| Configuração    | Valor | Significado                                     |
|-----------------|-------|-------------------------------------------------|
| `minSdk`        | 26    | Funciona em Android 8.0 Oreo ou mais novo       |
| `targetSdk`     | 35    | Otimizado para Android 15                       |
| `compileSdk`    | 35    | Compilado com as APIs do Android 15             |
| `jvmTarget`     | 11    | Bytecode compatível com Java 11                 |
| Gradle          | 9.3.1 | Versão do sistema de build                      |
| JDK Toolchain   | 21    | Versão do Java usada pelo daemon do Gradle       |

---

## 🔒 Permissões

O arquivo `AndroidManifest.xml` declara uma única permissão:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Necessária para que o app possa fazer chamadas HTTP à AwesomeAPI e se conectar ao Firebase.

---

## 📝 Observações Finais

- O projeto **não possui autenticação de usuário** — todos que tiverem acesso ao mesmo Firestore verão as mesmas despesas.
- A categoria **"Hospedagem"** está definida no modelo `Despesa.kt` (como comentário), mas a UI atual exibe apenas: Alimentação, Transporte e Lazer.
- O `google-services.json` **não deve ser enviado para repositórios públicos** — adicione-o ao `.gitignore` em produção.
- As regras do Firestore em **modo de teste expiram após 30 dias** — lembre-se de atualizar as regras antes disso.

---

*Projeto desenvolvido como trabalho universitário. Utiliza ferramentas e APIs gratuitas.*
