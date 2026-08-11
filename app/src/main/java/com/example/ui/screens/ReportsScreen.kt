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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockQuote
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReportsScreen(
    stock: StockQuote,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Report Header Banner
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
                            Icon(Icons.Default.Description, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("INSTITUTIONAL EQUITY RESEARCH MEMO", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Text("Ticker: ${stock.ticker} (${stock.name}) • Valuation & AI Thesis", fontSize = 11.sp, color = TextMuted)
                    }

                    Button(
                        onClick = { /* Export simulation */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export", tint = DarkSurface)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF Memo", color = DarkSurface, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Executive Summary
        item {
            ReportCard(
                title = "1. EXECUTIVE INVESTMENT THESIS",
                content = "${stock.ticker} presents an institutional BUY recommendation based on secular AI momentum, high gross margin expansion, and multi-quarter earnings surprises. Current valuation at P/E ${stock.peRatio} reflects top-tier growth premium, backed by $${stock.marketCap} market capitalization."
            )
        }

        // Financial & DCF Valuation Breakdown
        item {
            ReportCard(
                title = "2. DISCOUNTED CASH FLOW & VALUATION MULTIPLES",
                content = "Target Price: $${stock.priceTarget} (Implied Upside: ${String.format("%.1f", ((stock.priceTarget - stock.price) / stock.price) * 100)}%).\n\n• Base Case WACC: 8.5%\n• Terminal Growth Rate: 3.5%\n• Forward EV/EBITDA: 28.4x\n• Gross Profit Margin: ${stock.grossMargin}%\n• Operating Cash Flow Growth: +24.8% YoY"
            )
        }

        // Key Risks & Scenario Sensitivity
        item {
            ReportCard(
                title = "3. RISK FACTOR SENSITIVITY & BEAR CASE",
                content = "Primary Downside Risks:\n1. Macro interest rate persistence impacting tech valuation multiples.\n2. Supply chain constraint bottlenecks on next-generation architecture shipments.\n3. Regulatory antitrust scrutiny in key cross-border markets."
            )
        }
    }
}

@Composable
private fun ReportCard(title: String, content: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = CyanPrimary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}
