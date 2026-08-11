package com.example.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ForecastEntity
import com.example.data.model.StockQuote
import com.example.data.repository.MarketRepository
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PredictionsScreen(
    stock: StockQuote,
    forecastHistory: List<ForecastEntity>,
    onRunForecast: (String, String, Double, Double, Double, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val forecast = remember(stock) { MarketRepository.getForecastData(stock.ticker) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
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
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("QUANT MONTE CARLO & NEURAL MODEL PREDICTIONS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Text("Simulated 10,000 price path outcomes with 95% confidence bounds", fontSize = 11.sp, color = TextMuted)
                    }

                    Button(
                        onClick = {
                            onRunForecast(
                                stock.ticker,
                                "90-Day Neural Forecast",
                                forecast.predictedPrice,
                                forecast.bullScenario,
                                forecast.bearScenario,
                                forecast.confidenceScore
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DarkSurface)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Simulation", color = DarkSurface, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Current Forecast Metrics
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVE MODEL SCENARIOS FOR ${stock.ticker}", fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricItem("Spot Price", "$${stock.price}", TextPrimary)
                        MetricItem("Predicted Base", "$${String.format("%.2f", forecast.predictedPrice)}", CyanPrimary)
                        MetricItem("Bull Case (+)", "$${String.format("%.2f", forecast.bullScenario)}", BullishGreen)
                        MetricItem("Confidence Score", "${forecast.confidenceScore}%", GoldAccent)
                    }
                }
            }
        }

        // Explanation breakdown
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MODEL EXPLAINABILITY & DRIVERS", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    forecast.explainabilityFactors.forEach { factor ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ", color = CyanPrimary, fontWeight = FontWeight.Bold)
                            Text(factor, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Room Database Historical Forecast Logs
        item {
            Text("SAVED FORECAST HISTORY LOGS (${forecastHistory.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        }

        items(forecastHistory) { log ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${log.ticker} • ${log.modelType}", fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 13.sp)
                        Text("Base: $${log.predictedPrice} | Bull: $${log.bullCase}", fontSize = 11.sp, color = TextMuted)
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoldAccent.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                    ) {
                        Text("${log.confidence}% Conf.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(label, fontSize = 10.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
