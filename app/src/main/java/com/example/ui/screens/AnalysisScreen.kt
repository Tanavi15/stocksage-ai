package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candle
import com.example.data.model.RecommendationType
import com.example.data.model.StockQuote
import com.example.data.model.Timeframe
import com.example.data.repository.MarketRepository
import com.example.ui.components.StockHeaderCard
import com.example.ui.components.TradingViewChart
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    stock: StockQuote,
    onSelectStock: (StockQuote) -> Unit = {},
    isWatchlisted: Boolean = false,
    onWatchlistToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val quote = stock
    var selectedTimeframe by remember { mutableStateOf(Timeframe.D1) }
    var candles by remember { mutableStateOf<List<Candle>>(emptyList()) }

    LaunchedEffect(quote.ticker, selectedTimeframe) {
        candles = MarketRepository.getCandles(quote.ticker, selectedTimeframe)
    }

    val indicators = remember(candles) { MarketRepository.calculateIndicators(candles) }
    val aiDecision = remember(quote, indicators) { MarketRepository.getAiRecommendation(quote, indicators) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stock Header Summary
        item {
            StockHeaderCard(
                quote = quote,
                isWatchlisted = isWatchlisted,
                onWatchlistToggle = onWatchlistToggle
            )
        }

        // TradingView Style Interactive Chart Module
        item {
            TradingViewChart(
                symbol = quote.ticker,
                candles = candles,
                indicators = indicators,
                selectedTimeframe = selectedTimeframe,
                onTimeframeSelected = { selectedTimeframe = it }
            )
        }

        // AI Decision Engine Section
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_decision_engine_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI DECISION ENGINE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (aiDecision.rating) {
                                RecommendationType.STRONG_BUY, RecommendationType.BUY -> BullishGreen.copy(alpha = 0.25f)
                                RecommendationType.HOLD -> GoldAccent.copy(alpha = 0.25f)
                                else -> BearishRed.copy(alpha = 0.25f)
                            }
                        ) {
                            Text(
                                text = aiDecision.rating.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (aiDecision.rating) {
                                    RecommendationType.STRONG_BUY, RecommendationType.BUY -> BullishGreen
                                    RecommendationType.HOLD -> GoldAccent
                                    else -> BearishRed
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("CONFIDENCE SCORE", fontSize = 10.sp, color = TextMuted)
                            Text("${aiDecision.confidencePercent}%", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        }
                        Column {
                            Text("RISK PROFILE", fontSize = 10.sp, color = TextMuted)
                            Text(aiDecision.riskLevel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Column {
                            Text("BETA", fontSize = 10.sp, color = TextMuted)
                            Text("${quote.beta}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("EXPLAINABLE REASONING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    aiDecision.reasoning.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BullishGreen,
                                modifier = Modifier
                                    .padding(end = 6.dp, top = 2.dp)
                                    .height(14.dp)
                            )
                            Text(text = reason, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // Technical Indicators Summary Table
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TECHNICAL INDICATORS MATRIX", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IndicatorStat("RSI (14)", String.format("%.1f", indicators.rsi), if (indicators.rsi in 40.0..65.0) BullishGreen else GoldAccent)
                        IndicatorStat("EMA (20)", "$${String.format("%.2f", indicators.ema20)}", CyanPrimary)
                        IndicatorStat("EMA (50)", "$${String.format("%.2f", indicators.ema50)}", PurpleAccent)
                        IndicatorStat("MACD", String.format("%.2f", indicators.macdValue), if (indicators.macdHistogram > 0) BullishGreen else BearishRed)
                        IndicatorStat("VWAP", "$${String.format("%.2f", indicators.vwap)}", GoldAccent)
                        IndicatorStat("ATR", "$${String.format("%.2f", indicators.atr)}", TextPrimary)
                    }
                }
            }
        }

        // Company Description Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("COMPANY OVERVIEW", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(quote.description, fontSize = 12.sp, color = TextPrimary, lineHeight = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("CEO: ${quote.ceo} • Sector: ${quote.sector} • Industry: ${quote.industry}", fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun IndicatorStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(label, fontSize = 10.sp, color = TextMuted)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
