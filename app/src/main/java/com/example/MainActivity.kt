package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.local.ForecastEntity
import com.example.data.local.PortfolioEntity
import com.example.data.local.StockSageDatabase
import com.example.data.local.WatchlistEntity
import com.example.data.model.StockQuote
import com.example.data.repository.GeminiRepository
import com.example.data.repository.MarketRepository
import com.example.ui.components.GlobalSearchBar
import com.example.ui.components.NavigationSidebar
import com.example.ui.components.ScreenRoute
import com.example.ui.screens.AiCopilotScreen
import com.example.ui.screens.AnalysisScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ForecastScreen
import com.example.ui.screens.HeatmapScreen
import com.example.ui.screens.MarketsScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.PortfolioScreen
import com.example.ui.screens.PredictionsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.ScreenerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StockSageTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = StockSageDatabase.getDatabase(applicationContext)

        setContent {
            StockSageTheme {
                StockSageApp(db = db)
            }
        }
    }
}

@Composable
fun StockSageApp(db: StockSageDatabase) {
    val coroutineScope = rememberCoroutineScope()

    var currentRoute by remember { mutableStateOf(ScreenRoute.DASHBOARD) }
    var selectedStock by remember { mutableStateOf(MarketRepository.getStockQuote("NVDA")) }
    var apiKey by remember { mutableStateOf(BuildConfig.GEMINI_API_KEY) }

    var watchlistItems by remember { mutableStateOf<List<WatchlistEntity>>(emptyList()) }
    var portfolioItems by remember { mutableStateOf<List<PortfolioEntity>>(emptyList()) }
    var forecastHistory by remember { mutableStateOf<List<ForecastEntity>>(emptyList()) }

    // Load Room Data
    fun reloadRoomData() {
        coroutineScope.launch {
            val watch = withContext(Dispatchers.IO) { db.watchlistDao().getAllWatchlist() }
            val port = withContext(Dispatchers.IO) { db.portfolioDao().getAllPortfolio() }
            val fore = withContext(Dispatchers.IO) { db.forecastDao().getForecastHistory() }

            watchlistItems = watch
            portfolioItems = port
            forecastHistory = fore
        }
    }

    LaunchedEffect(Unit) {
        reloadRoomData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Navigation Sidebar
            NavigationSidebar(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route },
                modifier = Modifier.width(220.dp)
            )

            // Right Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(DarkBackground)
            ) {
                // Top Global Search and Live Market Ticker Bar
                TopTerminalHeader(
                    selectedStock = selectedStock,
                    onSelectStock = { quote ->
                        selectedStock = quote
                        if (currentRoute == ScreenRoute.DASHBOARD) {
                            currentRoute = ScreenRoute.ANALYSIS
                        }
                    }
                )

                // Active Main Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentRoute) {
                        ScreenRoute.DASHBOARD -> DashboardScreen(
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            },
                            onNavigateRoute = { route -> currentRoute = route }
                        )

                        ScreenRoute.ANALYSIS -> AnalysisScreen(
                            stock = selectedStock,
                            onSelectStock = { quote -> selectedStock = quote }
                        )

                        ScreenRoute.FORECAST -> ForecastScreen(
                            stock = selectedStock
                        )

                        ScreenRoute.AI_COPILOT -> AiCopilotScreen(
                            stock = selectedStock,
                            apiKey = apiKey
                        )

                        ScreenRoute.PORTFOLIO -> PortfolioScreen(
                            portfolioItems = portfolioItems,
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            },
                            onAddPosition = { ticker, shares, price, notes ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.portfolioDao().insertPosition(
                                            PortfolioEntity(
                                                ticker = ticker,
                                                shares = shares,
                                                buyPrice = price,
                                                notes = notes
                                            )
                                        )
                                    }
                                    reloadRoomData()
                                }
                            },
                            onRemovePosition = { ticker ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.portfolioDao().deletePosition(ticker)
                                    }
                                    reloadRoomData()
                                }
                            }
                        )

                        ScreenRoute.WATCHLIST -> WatchlistScreen(
                            watchlistItems = watchlistItems,
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            },
                            onAddWatchlist = { ticker, targetHigh, notes ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.watchlistDao().insertWatchlist(
                                            WatchlistEntity(
                                                ticker = ticker,
                                                targetAlertHigh = targetHigh,
                                                notes = notes
                                            )
                                        )
                                    }
                                    reloadRoomData()
                                }
                            },
                            onRemoveWatchlist = { ticker ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.watchlistDao().deleteWatchlist(ticker)
                                    }
                                    reloadRoomData()
                                }
                            }
                        )

                        ScreenRoute.MARKETS -> MarketsScreen(
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            }
                        )

                        ScreenRoute.SCREENER -> ScreenerScreen(
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            }
                        )

                        ScreenRoute.NEWS -> NewsScreen()

                        ScreenRoute.HEATMAP -> HeatmapScreen(
                            onSelectStock = { quote ->
                                selectedStock = quote
                                currentRoute = ScreenRoute.ANALYSIS
                            }
                        )

                        ScreenRoute.CALENDAR -> CalendarScreen()

                        ScreenRoute.REPORTS -> ReportsScreen(
                            stock = selectedStock
                        )

                        ScreenRoute.PREDICTIONS -> PredictionsScreen(
                            stock = selectedStock,
                            forecastHistory = forecastHistory,
                            onRunForecast = { ticker, modelType, pred, bull, bear, conf ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.forecastDao().insertForecast(
                                            ForecastEntity(
                                                ticker = ticker,
                                                modelType = modelType,
                                                predictedPrice = pred,
                                                bullCase = bull,
                                                bearCase = bear,
                                                confidence = conf
                                            )
                                        )
                                    }
                                    reloadRoomData()
                                }
                            }
                        )

                        ScreenRoute.SETTINGS -> SettingsScreen(
                            apiKey = apiKey,
                            onSaveApiKey = { newKey -> apiKey = newKey }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopTerminalHeader(
    selectedStock: StockQuote,
    onSelectStock: (StockQuote) -> Unit
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Selected Ticker Strip
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyanPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary)
                ) {
                    Text("ACTIVE TICKER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(selectedStock.ticker, fontWeight = FontWeight.ExtraBold, color = CyanPrimary, fontSize = 16.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Text("$${String.format("%.2f", selectedStock.price)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)

                Spacer(modifier = Modifier.width(8.dp))

                val isPos = selectedStock.change >= 0
                Text(
                    "${if (isPos) "+" else ""}${String.format("%.2f", selectedStock.changePercent)}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isPos) BullishGreen else BearishRed
                )
            }

            // Global Search Component
            GlobalSearchBar(
                onSelectStock = onSelectStock,
                modifier = Modifier
                    .width(320.dp)
                    .testTag("global_search_header")
            )
        }
    }
}
