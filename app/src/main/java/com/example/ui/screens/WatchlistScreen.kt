package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.WatchlistEntity
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
import com.example.ui.theme.TextSecondary

@Composable
fun WatchlistScreen(
    watchlistItems: List<WatchlistEntity>,
    onSelectStock: (StockQuote) -> Unit,
    onAddWatchlist: (String, Double?, String) -> Unit,
    onRemoveWatchlist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Watchlist Header Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WATCHLIST & PRICE TARGET ALERTS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Text("Monitored items with real-time signal triggers", fontSize = 11.sp, color = TextMuted)
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.testTag("add_watchlist_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = DarkSurface)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Ticker", color = DarkSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        if (watchlistItems.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your Watchlist is empty.", color = TextMuted, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onAddWatchlist("NVDA", 140.0, "Breakout alert target")
                                onAddWatchlist("AAPL", 240.0, "Apple Intelligence target")
                                onAddWatchlist("TSLA", 225.0, "Robotaxi launch target")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                        ) {
                            Text("Add Default Tech Watchlist", color = DarkSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(watchlistItems) { entity ->
                val quote = MarketRepository.getStockQuote(entity.ticker)
                val isPos = quote.change >= 0

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectStock(quote) }
                        .testTag("watchlist_item_${entity.ticker}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(quote.ticker, fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(quote.name, fontSize = 12.sp, color = TextMuted)
                            }
                            if (entity.targetAlertHigh != null) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent, modifier = Modifier.height(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Alert Target: $${entity.targetAlertHigh}", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (entity.notes.isNotBlank()) {
                                Text("Note: ${entity.notes}", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$${String.format("%.2f", quote.price)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Text(
                                    "${if (isPos) "+" else ""}${String.format("%.2f", quote.changePercent)}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPos) BullishGreen else BearishRed
                                )
                            }
                            IconButton(onClick = { onRemoveWatchlist(entity.ticker) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = BearishRed.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWatchlistDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { ticker, target, note ->
                onAddWatchlist(ticker, target, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddWatchlistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double?, String) -> Unit
) {
    var ticker by remember { mutableStateOf("MSFT") }
    var targetStr by remember { mutableStateOf("480.00") }
    var notes by remember { mutableStateOf("Target price alert level") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Watchlist Alert", color = GoldAccent, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ticker,
                    onValueChange = { ticker = it.uppercase() },
                    label = { Text("Stock Symbol (e.g. MSFT)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                    modifier = Modifier.fillMaxWidth().testTag("add_watchlist_ticker_input")
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Target Alert Price ($)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                    modifier = Modifier.fillMaxWidth().testTag("add_watchlist_target_input")
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Alert Notes") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
                    modifier = Modifier.fillMaxWidth().testTag("add_watchlist_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val t = targetStr.toDoubleOrNull()
                    onConfirm(ticker, t, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Add to Watchlist", color = DarkSurface, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        },
        containerColor = DarkSurface
    )
}
