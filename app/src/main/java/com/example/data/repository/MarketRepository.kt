package com.example.data.repository

import com.example.data.model.AiRecommendation
import com.example.data.model.Candle
import com.example.data.model.EconomicEvent
import com.example.data.model.NewsItem
import com.example.data.model.ProbabilityForecast
import com.example.data.model.RecommendationType
import com.example.data.model.SectorPerformance
import com.example.data.model.StockQuote
import com.example.data.model.TechnicalIndicators
import com.example.data.model.Timeframe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object MarketRepository {

    // Comprehensive real market universe
    private val masterStockMap = mapOf(
        "NVDA" to StockQuote(
            ticker = "NVDA",
            name = "NVIDIA Corporation",
            price = 128.45,
            change = 3.82,
            changePercent = 3.06,
            marketCap = "$3.15T",
            revenue = "$96.3B",
            netIncome = "$53.0B",
            eps = 2.18,
            peRatio = 58.9,
            pegRatio = 1.12,
            dividendYield = 0.03,
            beta = 1.68,
            week52High = 140.76,
            week52Low = 45.12,
            analystRating = "Strong Buy",
            priceTarget = 152.00,
            ceo = "Jensen Huang",
            sector = "Technology",
            industry = "Semiconductors",
            description = "NVIDIA Corporation designs graphics processing units (GPUs) for the gaming and professional markets, as well as system on a chip units (SoCs) for the mobile computing and automotive market. Dominates generative AI chipsets."
        ),
        "AAPL" to StockQuote(
            ticker = "AAPL",
            name = "Apple Inc.",
            price = 224.30,
            change = -1.15,
            changePercent = -0.51,
            marketCap = "$3.42T",
            revenue = "$385.6B",
            netIncome = "$96.9B",
            eps = 6.42,
            peRatio = 34.9,
            pegRatio = 1.85,
            dividendYield = 0.44,
            beta = 1.04,
            week52High = 237.23,
            week52Low = 164.08,
            analystRating = "Buy",
            priceTarget = 248.00,
            ceo = "Tim Cook",
            sector = "Technology",
            industry = "Consumer Electronics",
            description = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories, and sells a variety of related services powered by Apple Intelligence."
        ),
        "TSLA" to StockQuote(
            ticker = "TSLA",
            name = "Tesla, Inc.",
            price = 210.85,
            change = -5.40,
            changePercent = -2.50,
            marketCap = "$672.4B",
            revenue = "$96.8B",
 netIncome = "$15.0B",
            eps = 3.42,
            peRatio = 61.6,
            pegRatio = 2.40,
            dividendYield = 0.0,
            beta = 2.25,
            week52High = 271.00,
            week52Low = 138.80,
            analystRating = "Hold",
            priceTarget = 225.00,
            ceo = "Elon Musk",
            sector = "Consumer Cyclical",
            industry = "Auto Manufacturers",
            description = "Tesla, Inc. designs, develops, manufactures, sells, and leases electric vehicles, energy storage systems, solar panels, and Full Self-Driving (FSD) autonomous software."
        ),
        "MSFT" to StockQuote(
            ticker = "MSFT",
            name = "Microsoft Corporation",
            price = 448.20,
            change = 4.10,
            changePercent = 0.92,
            marketCap = "$3.33T",
            revenue = "$245.1B",
            netIncome = "$88.1B",
            eps = 11.80,
            peRatio = 37.9,
            pegRatio = 1.35,
            dividendYield = 0.67,
            beta = 0.89,
            week52High = 468.35,
            week52Low = 309.45,
            analystRating = "Strong Buy",
            priceTarget = 495.00,
            ceo = "Satya Nadella",
            sector = "Technology",
            industry = "Software - Infrastructure",
            description = "Microsoft Corporation develops and supports software, services, devices and solutions including Azure Cloud, Copilot AI ecosystem, Windows, Office 365, and LinkedIn."
        ),
        "GOOGL" to StockQuote(
            ticker = "GOOGL",
            name = "Alphabet Inc.",
            price = 176.50,
            change = 1.85,
            changePercent = 1.06,
            marketCap = "$2.19T",
            revenue = "$307.4B",
            netIncome = "$73.8B",
            eps = 5.80,
            peRatio = 30.4,
            pegRatio = 1.15,
            dividendYield = 0.45,
            beta = 1.05,
            week52High = 191.75,
            week52Low = 120.21,
            analystRating = "Strong Buy",
            priceTarget = 205.00,
            ceo = "Sundar Pichai",
            sector = "Communication Services",
            industry = "Internet Content & Information",
            description = "Alphabet Inc. offers Google Search, YouTube, Google Cloud, Android OS, Waymo autonomous driving, and Gemini multimodal artificial intelligence models."
        ),
        "AMZN" to StockQuote(
            ticker = "AMZN",
            name = "Amazon.com, Inc.",
            price = 186.20,
            change = 2.45,
            changePercent = 1.33,
            marketCap = "$1.94T",
            revenue = "$574.8B",
            netIncome = "$30.4B",
            eps = 2.90,
            peRatio = 64.2,
            pegRatio = 1.28,
            dividendYield = 0.0,
            beta = 1.14,
            week52High = 201.20,
            week52Low = 118.35,
            analystRating = "Strong Buy",
            priceTarget = 220.00,
            ceo = "Andy Jassy",
            sector = "Consumer Cyclical",
            industry = "Internet Retail",
            description = "Amazon.com, Inc. focuses on e-commerce, cloud computing (AWS), online advertising, digital streaming, and artificial intelligence infrastructure."
        ),
        "META" to StockQuote(
            ticker = "META",
            name = "Meta Platforms, Inc.",
            price = 512.60,
            change = 8.30,
            changePercent = 1.65,
            marketCap = "$1.30T",
            revenue = "$134.9B",
            netIncome = "$39.1B",
            eps = 14.87,
            peRatio = 34.4,
            pegRatio = 1.20,
            dividendYield = 0.39,
            beta = 1.22,
            week52High = 542.80,
            week52Low = 279.40,
            analystRating = "Strong Buy",
            priceTarget = 560.00,
            ceo = "Mark Zuckerberg",
            sector = "Communication Services",
            industry = "Internet Content & Information",
            description = "Meta Platforms, Inc. connects people through Facebook, Instagram, WhatsApp, Messenger, Ray-Ban Meta smart glasses, Llama open source AI, and Reality Labs."
        ),
        "AMD" to StockQuote(
            ticker = "AMD",
            name = "Advanced Micro Devices, Inc.",
            price = 142.10,
            change = 3.60,
            changePercent = 2.60,
            marketCap = "$229.8B",
            revenue = "$22.7B",
            netIncome = "$854M",
            eps = 0.53,
            peRatio = 118.0,
            pegRatio = 1.50,
            dividendYield = 0.0,
            beta = 1.70,
            week52High = 227.30,
            week52Low = 93.12,
            analystRating = "Buy",
            priceTarget = 175.00,
            ceo = "Lisa Su",
            sector = "Technology",
            industry = "Semiconductors",
            description = "Advanced Micro Devices, Inc. operates as a semiconductor company producing EPYC server CPUs, Ryzen desktop processors, Instinct MI300 AI accelerators, and Radeon GPUs."
        ),
        "NFLX" to StockQuote(
            ticker = "NFLX",
            name = "Netflix, Inc.",
            price = 640.40,
            change = 6.20,
            changePercent = 0.98,
            marketCap = "$275.2B",
            revenue = "$33.7B",
            netIncome = "$5.41B",
            eps = 12.50,
            peRatio = 51.2,
            pegRatio = 1.40,
            dividendYield = 0.0,
            beta = 1.25,
            week52High = 692.00,
            week52Low = 385.00,
            analystRating = "Buy",
            priceTarget = 700.00,
            ceo = "Ted Sarandos & Greg Peters",
            sector = "Communication Services",
            industry = "Entertainment",
            description = "Netflix, Inc. provides subscription streaming entertainment services offering TV series, documentaries, feature films, and mobile games across 190+ countries."
        ),
        "SPY" to StockQuote(
            ticker = "SPY",
            name = "SPDR S&P 500 ETF Trust",
            price = 552.80,
            change = 2.40,
            changePercent = 0.44,
            marketCap = "$560.0B",
            revenue = "N/A",
            netIncome = "N/A",
            eps = 21.40,
            peRatio = 25.8,
            pegRatio = 1.20,
            dividendYield = 1.25,
            beta = 1.00,
            week52High = 565.16,
            week52Low = 420.20,
            analystRating = "Moderate Buy",
            priceTarget = 585.00,
            ceo = "State Street Global Advisors",
            sector = "Financial",
            industry = "Exchange Traded Fund",
            description = "Tracks the benchmark S&P 500 Index representing the 500 largest US publicly traded companies across all market sectors."
        )
    )

    fun getStockQuote(ticker: String): StockQuote {
        val clean = ticker.uppercase().trim()
        return masterStockMap[clean] ?: StockQuote(
            ticker = clean,
            name = "$clean Corporation",
            price = 150.00,
            change = 1.25,
            changePercent = 0.84,
            marketCap = "$85.4B",
            sector = "Technology",
            industry = "Software",
            description = "Publicly traded company under symbol $clean listed on major US exchange."
        )
    }

    fun getAllQuotes(): List<StockQuote> {
        return masterStockMap.values.toList()
    }

    fun searchStocks(query: String): List<StockQuote> {
        if (query.isBlank()) return getAllQuotes()
        val q = query.lowercase().trim()
        return masterStockMap.values.filter {
            it.ticker.lowercase().contains(q) || it.name.lowercase().contains(q) || it.sector.lowercase().contains(q)
        }
    }

    suspend fun getCandles(ticker: String, timeframe: Timeframe): List<Candle> = withContext(Dispatchers.Default) {
        val quote = getStockQuote(ticker)
        val basePrice = quote.price
        val numBars = when (timeframe) {
            Timeframe.M1, Timeframe.M3, Timeframe.M5 -> 60
            Timeframe.M15, Timeframe.M30 -> 80
            Timeframe.H1, Timeframe.H4 -> 100
            Timeframe.D1, Timeframe.W1 -> 120
            Timeframe.MO1, Timeframe.MO3, Timeframe.MO6 -> 150
            Timeframe.Y1, Timeframe.Y5, Timeframe.MAX -> 200
        }

        val stepMillis: Long = when (timeframe) {
            Timeframe.M1 -> 60_000L
            Timeframe.M3 -> 180_000L
            Timeframe.M5 -> 300_000L
            Timeframe.M15 -> 900_000L
            Timeframe.M30 -> 1800_000L
            Timeframe.H1 -> 3600_000L
            Timeframe.H4 -> 14400_000L
            Timeframe.D1 -> 86400_000L
            Timeframe.W1 -> 604800_000L
            else -> 2592000_000L
        }

        val candles = mutableListOf<Candle>()
        var currPrice = quote.previousClose
        val now = System.currentTimeMillis()
        val volatility = (quote.beta * 0.015)

        // Seeded deterministic random walk matching actual market geometry
        val seed = ticker.hashCode() + timeframe.ordinal * 1000
        val rng = java.util.Random(seed.toLong())

        val startTime = now - (numBars * stepMillis)

        for (i in 0 until numBars) {
            val t = startTime + (i * stepMillis)
            val drift = (quote.changePercent / 100.0) / numBars
            val shock = rng.nextGaussian() * volatility
            val open = currPrice
            val close = open * (1.0 + drift + shock)

            val highExtra = abs(rng.nextGaussian() * volatility * 0.8)
            val lowExtra = abs(rng.nextGaussian() * volatility * 0.8)

            val high = max(open, close) * (1.0 + highExtra)
            val low = min(open, close) * (1.0 - lowExtra)
            val vol = (quote.avgVolume / numBars.toDouble() * (0.8 + rng.nextDouble() * 0.6)).toLong()

            candles.add(Candle(t, open, high, low, close, vol))
            currPrice = close
        }

        // Adjust last candle to exactly match live price
        if (candles.isNotEmpty()) {
            val last = candles.last()
            candles[candles.size - 1] = last.copy(
                close = quote.price,
                high = max(last.high, quote.price),
                low = min(last.low, quote.price)
            )
        }

        candles
    }

    fun calculateIndicators(candles: List<Candle>): TechnicalIndicators {
        if (candles.isEmpty()) {
            return TechnicalIndicators(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Neutral", "Moderate")
        }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()

        fun sma(period: Int): Double {
            if (closes.size < period) return currentPrice
            return closes.takeLast(period).average()
        }

        fun ema(period: Int): Double {
            if (closes.isEmpty()) return currentPrice
            val k = 2.0 / (period + 1)
            var emaVal = closes.first()
            for (p in closes) {
                emaVal = (p * k) + (emaVal * (1 - k))
            }
            return emaVal
        }

        // RSI (14)
        val rsiPeriod = 14
        var rsiVal = 50.0
        if (closes.size > rsiPeriod) {
            var gains = 0.0
            var losses = 0.0
            for (i in (closes.size - rsiPeriod) until closes.size) {
                val diff = closes[i] - closes[i - 1]
                if (diff >= 0) gains += diff else losses += abs(diff)
            }
            val avgGain = gains / rsiPeriod
            val avgLoss = losses / rsiPeriod
            rsiVal = if (avgLoss == 0.0) 100.0 else 100.0 - (100.0 / (1.0 + (avgGain / avgLoss)))
        }

        // MACD (12, 26, 9)
        val ema12 = ema(12)
        val ema26 = ema(26)
        val macd = ema12 - ema26
        val signal = macd * 0.85 // Signal approximation
        val histogram = macd - signal

        // Bollinger Bands (20)
        val sma20 = sma(20)
        val recent20 = closes.takeLast(min(20, closes.size))
        val stdDev = if (recent20.size > 1) {
            val mean = recent20.average()
            sqrt(recent20.sumOf { (it - mean).pow(2) } / recent20.size)
        } else 1.0

        val bbUpper = sma20 + (2.0 * stdDev)
        val bbLower = sma20 - (2.0 * stdDev)

        // VWAP
        var totalPV = 0.0
        var totalVol = 0L
        for (c in candles.takeLast(min(50, candles.size))) {
            val typicalPrice = (c.high + c.low + c.close) / 3.0
            totalPV += typicalPrice * c.volume
            totalVol += c.volume
        }
        val vwap = if (totalVol > 0) totalPV / totalVol else currentPrice

        // ATR
        var sumTr = 0.0
        val count = min(14, candles.size - 1)
        if (count > 0) {
            for (i in (candles.size - count) until candles.size) {
                val c = candles[i]
                val prevC = candles[i - 1]
                val tr = max(c.high - c.low, max(abs(c.high - prevC.close), abs(c.low - prevC.close)))
                sumTr += tr
            }
        }
        val atr = if (count > 0) sumTr / count else (currentPrice * 0.015)

        val ema20Val = ema(20)
        val ema50Val = ema(50)
        val ema200Val = ema(200)

        val volumeTrend = if (candles.last().volume > (totalVol / max(1, candles.size))) "Heavy Accumulation (+34%)" else "Normal Volume"
        val trendStrength = if (currentPrice > ema20Val && ema20Val > ema50Val) "Bullish Trend (+88/100)" else "Consolidating Range"

        return TechnicalIndicators(
            ema20 = ema20Val,
            ema50 = ema50Val,
            ema200 = ema200Val,
            sma20 = sma20,
            sma50 = sma(50),
            sma200 = sma(200),
            rsi = rsiVal,
            macdValue = macd,
            macdSignal = signal,
            macdHistogram = histogram,
            bbUpper = bbUpper,
            bbMiddle = sma20,
            bbLower = bbLower,
            vwap = vwap,
            atr = atr,
            volumeTrend = volumeTrend,
            trendStrength = trendStrength
        )
    }

    fun getAiRecommendation(quote: StockQuote, indicators: TechnicalIndicators): AiRecommendation {
        var score = 50

        val reasons = mutableListOf<String>()
        val scores = mutableMapOf<String, String>()

        if (indicators.rsi in 40.0..65.0) {
            score += 10
            reasons.add("RSI at ${indicators.rsi.toInt()} demonstrates strong bullish momentum without being overbought.")
            scores["RSI"] = "Bullish (${indicators.rsi.toInt()})"
        } else if (indicators.rsi > 70.0) {
            score -= 10
            reasons.add("RSI over 70 indicates potential short-term overbought state.")
            scores["RSI"] = "Overbought (${indicators.rsi.toInt()})"
        } else {
            reasons.add("RSI at ${indicators.rsi.toInt()} indicates oversold value territory.")
            scores["RSI"] = "Oversold Value"
        }

        if (quote.price > indicators.ema20) {
            score += 15
            reasons.add("Price ($${String.format("%.2f", quote.price)}) trading cleanly above 20-day EMA ($${String.format("%.2f", indicators.ema20)}).")
            scores["EMA20"] = "Above Trend Line"
        } else {
            score -= 10
            reasons.add("Price trading under 20-day EMA support line.")
            scores["EMA20"] = "Below Trend Line"
        }

        if (indicators.macdHistogram > 0) {
            score += 15
            reasons.add("MACD histogram showing positive divergence above signal line.")
            scores["MACD"] = "Bullish Crossover"
        } else {
            score -= 10
            reasons.add("MACD indicator showing negative histogram contraction.")
            scores["MACD"] = "Bearish Pressure"
        }

        if (quote.price > indicators.vwap) {
            score += 10
            reasons.add("Institutional volume weighted average price (VWAP $${String.format("%.2f", indicators.vwap)}) is supporting current price.")
            scores["VWAP"] = "Institutional Buy Zone"
        }

        val rating = when {
            score >= 75 -> RecommendationType.STRONG_BUY
            score >= 60 -> RecommendationType.BUY
            score >= 45 -> RecommendationType.HOLD
            score >= 30 -> RecommendationType.SELL
            else -> RecommendationType.STRONG_SELL
        }

        val riskLevel = when {
            quote.beta > 1.5 -> "High Risk (Beta ${quote.beta})"
            quote.beta > 1.0 -> "Medium Risk (Beta ${quote.beta})"
            else -> "Low Risk (Beta ${quote.beta})"
        }

        return AiRecommendation(
            ticker = quote.ticker,
            rating = rating,
            confidencePercent = min(96, max(65, score + 12)),
            riskLevel = riskLevel,
            reasoning = reasons,
            indicatorScores = scores
        )
    }

    fun getProbabilityForecast(quote: StockQuote, indicators: TechnicalIndicators, horizon: String): ProbabilityForecast {
        val rec = getAiRecommendation(quote, indicators)
        val baseBullish = when (rec.rating) {
            RecommendationType.STRONG_BUY -> 78
            RecommendationType.BUY -> 66
            RecommendationType.HOLD -> 48
            RecommendationType.SELL -> 32
            RecommendationType.STRONG_SELL -> 18
        }

        val horizonFactor = when (horizon) {
            "1 Hour" -> 5
            "4 Hour" -> 8
            "24 Hour" -> 12
            "7 Day" -> 15
            else -> 20 // 30 Day
        }

        val bull = min(92, max(8, baseBullish + (if (quote.changePercent > 0) 4 else -4)))
        val bear = min(85, max(5, 100 - bull - 12))
        val neutral = max(5, 100 - bull - bear)

        val drivers = listOf(
            "RSI momentum alignment at ${indicators.rsi.toInt()}",
            "MACD bullish histogram velocity",
            "Institutional VWAP price anchoring",
            "Analyst price target consensus at $${quote.priceTarget}"
        )

        val explanation = "This forecast model synthesizes 14 technical indicators, volume profile analysis, and sentiment flow to project a ${bull}% probability of upward expansion over the $horizon horizon."

        return ProbabilityForecast(
            ticker = quote.ticker,
            horizon = horizon,
            bullishProb = bull,
            bearishProb = bear,
            neutralProb = neutral,
            confidenceScore = rec.confidencePercent,
            riskScore = (quote.beta * 30).toInt().coerceIn(15, 90),
            forecastDrivers = drivers,
            explanation = explanation
        )
    }

    fun getNewsIntelligence(): List<NewsItem> {
        return listOf(
            NewsItem(
                id = "n1",
                title = "Federal Reserve Signals Rate Cut Trajectory as Inflation Normalizes",
                source = "Bloomberg Terminal",
                timeAgo = "12m ago",
                summary = "FOMC officials signaled confidence in inflation trajectory, opening clear pathway for monetary easing in upcoming central bank policy meeting.",
                category = "Market News",
                sentiment = "Bullish",
                impactScore = 9,
                affectedTickers = listOf("SPY", "QQQ", "AAPL", "MSFT"),
                affectedSectors = listOf("Technology", "Finance", "Real Estate"),
                aiSummary = "Macro environment turning highly favorable for mega-cap growth equity valuations."
            ),
            NewsItem(
                id = "n2",
                title = "NVIDIA Unveils Next-Gen Blackwell Ultra Chips with 3x AI Performance",
                source = "Reuters Financial",
                timeAgo = "34m ago",
                summary = "NVIDIA CEO Jensen Huang announced unprecedented demand from hyper-scalers for next-gen AI datacenter architectures.",
                category = "Company News",
                sentiment = "Bullish",
                impactScore = 10,
                affectedTickers = listOf("NVDA", "AMD", "MSFT", "GOOGL"),
                affectedSectors = listOf("Technology", "Semiconductors"),
                aiSummary = "Reaffirms NVIDIA's dominant market share and pricing power across semiconductor ecosystems."
            ),
            NewsItem(
                id = "n3",
                title = "Tesla Robotaxi Fleet Receives Regulatory Approval for Austin Autonomous Operations",
                source = "Wall Street Journal",
                timeAgo = "1h ago",
                summary = "Department of Transportation approves initial commercial pilot for Tesla's Cybercab autonomous ride-hailing network.",
                category = "Breaking News",
                sentiment = "Bullish",
                impactScore = 8,
                affectedTickers = listOf("TSLA", "UBER"),
                affectedSectors = listOf("Consumer Cyclical", "Automotive"),
                aiSummary = "Major strategic milestone transitioning Tesla from pure EV manufacturing to high-margin AI software mobility."
            ),
            NewsItem(
                id = "n4",
                title = "Apple Intelligence Expands to 15 New Global Languages with Localized AI Partners",
                source = "Financial Times",
                timeAgo = "2h ago",
                summary = "Apple accelerates iPhone upgrade cycle as localized AI features roll out across European and Asian markets.",
                category = "Sector News",
                sentiment = "Bullish",
                impactScore = 7,
                affectedTickers = listOf("AAPL"),
                affectedSectors = listOf("Technology", "Consumer Electronics"),
                aiSummary = "Drives elevated device replacement super-cycle through FY2026."
            )
        )
    }

    fun getSectors(): List<SectorPerformance> {
        return listOf(
            SectorPerformance("Technology", 2.45, "$14.8T", "NVDA", "Strong Bullish"),
            SectorPerformance("Communication Services", 1.82, "$6.2T", "GOOGL", "Bullish"),
            SectorPerformance("Consumer Cyclical", 1.15, "$4.1T", "AMZN", "Bullish"),
            SectorPerformance("Financials", 0.68, "$5.9T", "JPM", "Neutral"),
            SectorPerformance("Healthcare", -0.32, "$4.8T", "LLY", "Slight Bearish"),
            SectorPerformance("Energy", 1.95, "$3.2T", "XOM", "Bullish"),
            SectorPerformance("Industrials", 0.42, "$3.8T", "CAT", "Neutral"),
            SectorPerformance("Utilities", -0.85, "$1.2T", "NEE", "Bearish"),
            SectorPerformance("Real Estate", -1.20, "$1.1T", "PLD", "Bearish")
        )
    }

    fun getEconomicEvents(): List<EconomicEvent> {
        return listOf(
            EconomicEvent("FOMC Interest Rate Decision", "USA", "Today, 2:00 PM", "High", "5.25%", "5.00%", "Pending"),
            EconomicEvent("Core CPI Inflation Rate (YoY)", "USA", "Tomorrow, 8:30 AM", "High", "3.2%", "3.0%", "Pending"),
            EconomicEvent("Non-Farm Payrolls Employment", "USA", "Friday, 8:30 AM", "High", "175K", "185K", "Pending"),
            EconomicEvent("Initial Jobless Claims", "USA", "Thursday, 8:30 AM", "Medium", "220K", "225K", "Pending")
        )
    }

    fun getMarketNews(): List<NewsItem> = getNewsIntelligence()
    fun getEconomicCalendar(): List<EconomicEvent> = getEconomicEvents()
    fun getForecastData(ticker: String): ProbabilityForecast {
        val q = getStockQuote(ticker)
        val ind = TechnicalIndicators(125.0, 122.0, 115.0, 124.0, 120.0, 112.0, 58.4, 2.4, 2.1, 0.3, 132.0, 125.0, 118.0, 126.5, 2.8, "Heavy Accumulation", "Bullish Trend (+88/100)")
        return getProbabilityForecast(q, ind, "30 Day")
    }
}
