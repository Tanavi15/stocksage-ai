package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenerScreen(
    onSelectStock: (StockQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    val allQuotes = MarketRepository.getAllQuotes()

    var selectedSector by remember { mutableStateOf("All") }
    var selectedRating by remember { mutableStateOf("All") }
    var minPeFilter by remember { mutableStateOf("All") }

    val filteredQuotes = remember(allQuotes, selectedSector, selectedRating, minPeFilter) {
        allQuotes.filter { q ->
            val sectorMatch = selectedSector == "All" || q.sector == selectedSector
            val ratingMatch = selectedRating == "All" || q.analystRating.contains(selectedRating, ignoreCase = true)
            val peMatch = when (minPeFilter) {
                "< 35" -> q.peRatio < 35.0
                "35 - 60" -> q.peRatio in 35.0..60.0
                "> 60" -> q.peRatio > 60.0
                else -> true
            }
            sectorMatch && ratingMatch && peMatch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screener Filter Header
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FilterAlt, contentDescription = null, tint = CyanPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INSTITUTIONAL QUANT SCREENER", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("SECTOR FILTER", fontSize = 10.sp, color = TextMuted)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        listOf("All", "Technology", "Consumer Cyclical", "Communication Services").forEach { sec ->
                            val isSel = sec == selectedSector
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedSector = sec },
                                label = { Text(sec, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                                    selectedLabelColor = CyanPrimary,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("ANALYST RATING FILTER", fontSize = 10.sp, color = TextMuted)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        listOf("All", "Strong Buy", "Buy", "Hold").forEach { r ->
                            val isSel = r == selectedRating
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedRating = r },
                                label = { Text(r, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                                    selectedLabelColor = CyanPrimary,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("P/E VALUATION RANGE", fontSize = 10.sp, color = TextMuted)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        listOf("All", "< 35", "35 - 60", "> 60").forEach { pe ->
                            val isSel = pe == minPeFilter
                            FilterChip(
                                selected = isSel,
                                onClick = { minPeFilter = pe },
                                label = { Text(pe, fontSize = 11.sp) },
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

        // Screener Results Count
        item {
            Text("MATCHING SCREENER RESULTS (${filteredQuotes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        }

        items(filteredQuotes) { quote ->
            val isPos = quote.change >= 0
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectStock(quote) }
                    .testTag("screener_result_${quote.ticker}")
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
                        Text("${quote.sector} • P/E ${quote.peRatio} • Target $${quote.priceTarget}", fontSize = 10.sp, color = TextMuted)
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
