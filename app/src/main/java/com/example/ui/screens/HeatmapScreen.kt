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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeatmapScreen(
    onSelectStock: (StockQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    val quotes = MarketRepository.getAllQuotes()
    val sectorGroups = quotes.groupBy { it.sector }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MARKET SECTOR HEATMAP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                    Text("Color density represents percentage gain / loss intensity", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        sectorGroups.forEach { (sectorName, sectorQuotes) ->
            item {
                Column {
                    Text(sectorName.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sectorQuotes.forEach { stock ->
                            val isPos = stock.change >= 0
                            val alpha = (kotlin.math.abs(stock.changePercent) / 5.0).coerceIn(0.2, 0.9).toFloat()
                            val tileColor = if (isPos) BullishGreen.copy(alpha = alpha) else BearishRed.copy(alpha = alpha)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tileColor,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                                modifier = Modifier
                                    .width(105.dp)
                                    .height(75.dp)
                                    .clickable { onSelectStock(stock) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(stock.ticker, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = TextPrimary)
                                    Text("$${String.format("%.1f", stock.price)}", fontSize = 10.sp, color = TextPrimary.copy(alpha = 0.9f))
                                    Text(
                                        "${if (isPos) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
