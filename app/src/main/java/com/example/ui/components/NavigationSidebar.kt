package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NavigationItem
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

object ScreenRoute {
    const val DASHBOARD = "dashboard"
    const val MARKETS = "markets"
    const val ANALYSIS = "analysis"
    const val FORECAST = "forecast"
    const val AI_COPILOT = "copilot"
    const val PORTFOLIO = "portfolio"
    const val WATCHLIST = "watchlist"
    const val SCREENER = "screener"
    const val NEWS = "news"
    const val HEATMAP = "heatmap"
    const val CALENDAR = "calendar"
    const val REPORTS = "reports"
    const val PREDICTIONS = "predictions"
    const val SETTINGS = "settings"
}

@Composable
fun NavigationSidebar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // App Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(
                        text = "SX",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Column {
                    Text(
                        text = "STOCKSAGE X",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "AI Market Terminal",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Items List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavigationItem.entries.forEach { item ->
                    val isSelected = item.route == currentRoute
                    val icon = getIconForName(item.iconName)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) CyanPrimary.copy(alpha = 0.15f) else DarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 10.dp, vertical = 9.dp)
                            .testTag("sidebar_item_${item.route}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = item.title,
                                tint = if (isSelected) CyanPrimary else TextSecondary,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                text = item.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyanPrimary else TextPrimary
                            )
                        }
                    }
                }
            }

            // Bottom Status Card
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(CyanPrimary, shape = RoundedCornerShape(4.dp))
                    )
                    Column {
                        Text("PRO TERMINAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        Text("Live Market Stream", fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

private fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Dashboard" -> Icons.Default.Dashboard
        "TrendingUp" -> Icons.Default.TrendingUp
        "ShowChart" -> Icons.Default.ShowChart
        "Psychology" -> Icons.Default.Psychology
        "SmartToy" -> Icons.Default.SmartToy
        "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
        "Star" -> Icons.Default.Star
        "FilterAlt" -> Icons.Default.FilterAlt
        "Newspaper" -> Icons.Default.Newspaper
        "GridView" -> Icons.Default.GridView
        "Event" -> Icons.Default.Event
        "Assessment" -> Icons.Default.Assessment
        "CheckCircle" -> Icons.Default.CheckCircle
        else -> Icons.Default.Settings
    }
}
