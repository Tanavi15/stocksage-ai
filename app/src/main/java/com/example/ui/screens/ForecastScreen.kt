package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.StockQuote
import com.example.data.repository.MarketRepository
import com.example.ui.components.GaugeChart
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ForecastScreen(
    quote: StockQuote = MarketRepository.getAllQuotes().first(),
    stock: StockQuote = quote,
    modifier: Modifier = Modifier
) {
    val activeQuote = stock
    val horizons = listOf("1 Hour", "4 Hour", "24 Hour", "7 Day", "30 Day")
    var selectedHorizon by remember { mutableStateOf("7 Day") }

    val candles = remember(activeQuote.ticker) {
        kotlinx.coroutines.runBlocking { MarketRepository.getCandles(activeQuote.ticker, com.example.data.model.Timeframe.D1) }
    }
    val indicators = remember(candles) { MarketRepository.calculateIndicators(candles) }
    val forecast = remember(activeQuote, selectedHorizon) {
        MarketRepository.getProbabilityForecast(activeQuote, indicators, selectedHorizon)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizon Selector Pill Bar
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("PROBABILISTIC FORECAST HORIZON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        horizons.forEach { h ->
                            val isSel = h == selectedHorizon
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedHorizon = h },
                                label = { Text(h, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                                    selectedLabelColor = CyanPrimary,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Probability Distribution Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("probability_forecast_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${quote.ticker} • $selectedHorizon OUTLOOK", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent.copy(alpha = 0.2f)
                        ) {
                            Text("PROBABILITY MODEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GaugeChart(
                            score = forecast.bullishProb,
                            title = "BULLISH",
                            subtitle = "${forecast.bullishProb}% Probability"
                        )
                        GaugeChart(
                            score = forecast.bearishProb,
                            title = "BEARISH",
                            subtitle = "${forecast.bearishProb}% Probability"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Probability Bar
                    Column {
                        Text("PROBABILITY SPECTRUM", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(DarkSurfaceVariant, shape = RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(forecast.bullishProb.toFloat().coerceAtLeast(1f))
                                    .height(12.dp)
                                    .background(BullishGreen, shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(forecast.neutralProb.toFloat().coerceAtLeast(1f))
                                    .height(12.dp)
                                    .background(GoldAccent)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(forecast.bearishProb.toFloat().coerceAtLeast(1f))
                                    .height(12.dp)
                                    .background(BearishRed, shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bullish ${forecast.bullishProb}%", fontSize = 10.sp, color = BullishGreen, fontWeight = FontWeight.Bold)
                            Text("Neutral ${forecast.neutralProb}%", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            Text("Bearish ${forecast.bearishProb}%", fontSize = 10.sp, color = BearishRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Forecast Drivers & Explanation
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("KEY FORECAST DRIVERS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    forecast.forecastDrivers.forEach { driver ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("• ", color = CyanPrimary, fontWeight = FontWeight.Bold)
                            Text(driver, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("MODEL EXPLANATION", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(forecast.explanation, fontSize = 12.sp, color = TextPrimary, lineHeight = 18.sp)
                }
            }
        }
    }
}
