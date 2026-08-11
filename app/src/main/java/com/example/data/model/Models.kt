package com.example.data.model

import androidx.annotation.Keep

enum class NavigationItem(val route: String, val title: String, val iconName: String) {
    DASHBOARD("dashboard", "Dashboard", "Dashboard"),
    MARKETS("markets", "Markets", "TrendingUp"),
    ANALYSIS("analysis", "Analysis", "ShowChart"),
    FORECAST("forecast", "Forecast Center", "Psychology"),
    COPILOT("copilot", "AI Copilot", "SmartToy"),
    PORTFOLIO("portfolio", "Portfolio", "AccountBalanceWallet"),
    WATCHLIST("watchlist", "Watchlist", "Star"),
    SCREENER("screener", "Stock Screener", "FilterAlt"),
    NEWS("news", "News Intelligence", "Newspaper"),
    HEATMAP("heatmap", "Sector Heatmap", "GridView"),
    CALENDAR("calendar", "Calendar", "Event"),
    REPORTS("reports", "Reports Generator", "Assessment"),
    PREDICTIONS("predictions", "Prediction Accuracy", "CheckCircle"),
    SETTINGS("settings", "Settings", "Settings")
}

@Keep
data class StockQuote(
    val ticker: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val open: Double = price * 0.995,
    val high: Double = price * 1.012,
    val low: Double = price * 0.988,
    val previousClose: Double = price - change,
    val volume: Long = 45210900L,
    val avgVolume: Long = 52100000L,
    val marketCap: String = "$3.12T",
    val enterpriseValue: String = "$3.08T",
    val revenue: String = "$385.6B",
    val netIncome: String = "$96.9B",
    val eps: Double = 6.42,
    val peRatio: Double = 31.8,
    val pegRatio: Double = 1.45,
    val dividendYield: Double = 0.52,
    val beta: Double = 1.18,
    val grossMargin: Double = 72.5,
    val week52High: Double = price * 1.2,
    val week52Low: Double = price * 0.75,
    val analystRating: String = "Strong Buy",
    val priceTarget: Double = price * 1.22,
    val ceo: String = "Tim Cook",
    val sector: String = "Technology",
    val industry: String = "Consumer Electronics",
    val description: String = "Designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories, and sells a variety of related services."
)

@Keep
data class Candle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

enum class Timeframe(val code: String, val label: String) {
    M1("1m", "1M"),
    M3("3m", "3M"),
    M5("5m", "5M"),
    M15("15m", "15M"),
    M30("30m", "30M"),
    H1("1h", "1H"),
    H4("4h", "4H"),
    D1("1d", "1D"),
    W1("1w", "1W"),
    MO1("1mo", "1MO"),
    MO3("3mo", "3MO"),
    MO6("6mo", "6MO"),
    Y1("1y", "1Y"),
    Y5("5y", "5Y"),
    MAX("max", "MAX")
}

@Keep
data class TechnicalIndicators(
    val ema20: Double,
    val ema50: Double,
    val ema200: Double,
    val sma20: Double,
    val sma50: Double,
    val sma200: Double,
    val rsi: Double,
    val macdValue: Double,
    val macdSignal: Double,
    val macdHistogram: Double,
    val bbUpper: Double,
    val bbMiddle: Double,
    val bbLower: Double,
    val vwap: Double,
    val atr: Double,
    val volumeTrend: String,
    val trendStrength: String
)

enum class RecommendationType(val label: String) {
    STRONG_BUY("STRONG BUY"),
    BUY("BUY"),
    HOLD("HOLD"),
    SELL("SELL"),
    STRONG_SELL("STRONG SELL")
}

@Keep
data class AiRecommendation(
    val ticker: String,
    val rating: RecommendationType,
    val confidencePercent: Int,
    val riskLevel: String, // Low, Medium, High
    val reasoning: List<String>,
    val indicatorScores: Map<String, String>
)

@Keep
data class ProbabilityForecast(
    val ticker: String,
    val horizon: String, // 1 Hour, 4 Hour, 24 Hour, 7 Day, 30 Day
    val bullishProb: Int,
    val bearishProb: Int,
    val neutralProb: Int,
    val confidenceScore: Int,
    val riskScore: Int,
    val forecastDrivers: List<String>,
    val explanation: String
) {
    val predictedPrice: Double get() = 135.50
    val bullScenario: Double get() = 155.00
    val bearScenario: Double get() = 115.00
    val explainabilityFactors: List<String> get() = forecastDrivers
}

@Keep
data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val summary: String,
    val category: String, // Breaking, Company, Sector, Market
    val sentiment: String, // Bullish, Bearish, Neutral
    val impactScore: Int, // 1 to 10
    val affectedTickers: List<String>,
    val affectedSectors: List<String>,
    val aiSummary: String
) {
    val headline: String get() = title
    val relatedTickers: List<String> get() = affectedTickers
}

@Keep
data class SectorPerformance(
    val sectorName: String,
    val changePercent: Double,
    val marketCap: String,
    val topTicker: String,
    val sentiment: String
)

@Keep
data class EconomicEvent(
    val title: String,
    val country: String,
    val date: String,
    val impactLevel: String, // High, Medium, Low
    val previous: String,
    val forecast: String,
    val actual: String
) {
    val category: String get() = country
    val impact: String get() = impactLevel
    val consensus: String get() = forecast
}

@Keep
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "COPILOT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isThinking: Boolean = false,
    val relatedTickers: List<String> = emptyList()
)
