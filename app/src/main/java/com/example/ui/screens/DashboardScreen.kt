package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    onSelectStock: (StockQuote) -> Unit,
    onNavigateRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allQuotes = MarketRepository.getAllQuotes()
    val topGainers = allQuotes.sortedByDescending { it.changePercent }.take(4)
    val topLosers = allQuotes.sortedBy { it.changePercent }.take(4)
    val sectors = MarketRepository.getSectors()
    val events = MarketRepository.getEconomicEvents()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner: Market Status & AI Market Summary
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(BullishGreen, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("MARKET OPEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkSurface)
                            }
                            Text("NYSE / NASDAQ LIVE", fontSize = 11.sp, color = TextMuted)
                        }
                        Text("AI MARKET INTELLIGENCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Summary",
                            tint = GoldAccent,
                            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                        )
                        Text(
                            text = "US Equities rally led by semiconductor momentum and FOMC dovish commentary. NASDAQ composite up +1.28% driven by record datacenter order backlogs and easing rate expectations.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Major Indices Cards
        item {
            Text("MAJOR MARKET INDICES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item { IndexCard("S&P 500", "5,528.40", "+24.20 (+0.44%)", true) }
                item { IndexCard("NASDAQ", "17,890.15", "+228.40 (+1.28%)", true) }
                item { IndexCard("DOW JONES", "39,450.80", "-45.10 (-0.11%)", false) }
                item { IndexCard("RUSSELL 2000", "2,180.40", "+14.60 (+0.67%)", true) }
            }
        }

        // Fear & Greed Gauge + Sector Performance Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fear & Greed Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text("FEAR & GREED INDEX", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        GaugeChart(
                            score = 74,
                            title = "GREED",
                            subtitle = "Bullish Momentum"
                        )
                    }
                }

                // Sector Performance Overview Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("TOP SECTORS TODAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        sectors.take(4).forEach { sec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sec.sectorName, fontSize = 11.sp, color = TextPrimary)
                                val isPos = sec.changePercent >= 0
                                Text(
                                    "${if (isPos) "+" else ""}${sec.changePercent}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPos) BullishGreen else BearishRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Gainers & Market Movers Grid
        item {
            Text("TOP MARKET MOVERS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                topGainers.forEach { quote ->
                    StockRowCard(quote = quote, onClick = { onSelectStock(quote) })
                }
            }
        }

        // Upcoming Economic Events
        item {
            Text("ECONOMIC CALENDAR EVENTS TODAY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    events.forEach { ev ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ev.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${ev.country} • ${ev.date}", fontSize = 10.sp, color = TextMuted)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(ev.impactLevel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndexCard(name: String, value: String, change: String, isPos: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.width(140.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(change, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isPos) BullishGreen else BearishRed)
        }
    }
}

@Composable
fun StockRowCard(quote: StockQuote, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("stock_row_${quote.ticker}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        quote.ticker,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Column {
                    Text(quote.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("${quote.sector} • Cap ${quote.marketCap}", fontSize = 10.sp, color = TextMuted)
                }
            }

            val isPos = quote.change >= 0
            Column(horizontalAlignment = Alignment.End) {
                Text("$${String.format("%.2f", quote.price)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "${if (isPos) "+" else ""}${String.format("%.2f", quote.changePercent)}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPos) BullishGreen else BearishRed
                )
            }
        }
    }
}
