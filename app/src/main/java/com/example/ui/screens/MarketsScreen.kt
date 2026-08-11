package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockQuote
import com.example.data.repository.MarketRepository
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun MarketsScreen(
    onSelectStock: (StockQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    val quotes = MarketRepository.getAllQuotes()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Global Markets Header
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GLOBAL MULTI-ASSET STREAM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                    Text("Real-time pricing for equities, commodities, and currency benchmarks", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        // Macro Indicators & Commodities Row
        item {
            Text("COMMODITIES & BENCHMARKS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MacroPill("GOLD", "$2,450.80", "+1.15%", true, modifier = Modifier.weight(1f))
                MacroPill("BRENT OIL", "$82.40", "-0.85%", false, modifier = Modifier.weight(1f))
                MacroPill("US 10Y YIELD", "4.21%", "-0.04%", false, modifier = Modifier.weight(1f))
                MacroPill("BITCOIN", "$64,820", "+3.40%", true, modifier = Modifier.weight(1f))
            }
        }

        // US Equities Master List
        item {
            Text("EQUITIES UNIVERSE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        }

        items(quotes) { quote ->
            val isPos = quote.change >= 0
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectStock(quote) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(quote.ticker, fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(quote.name, fontSize = 12.sp, color = TextMuted)
                        }
                        Text("${quote.sector} • P/E ${quote.peRatio} • Rating: ${quote.analystRating}", fontSize = 10.sp, color = TextMuted)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("$${String.format("%.2f", quote.price)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
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
    }
}

@Composable
private fun MacroPill(name: String, price: String, change: String, isPos: Boolean, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(price, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(change, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isPos) BullishGreen else BearishRed)
        }
    }
}
