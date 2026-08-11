package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candle
import com.example.data.model.TechnicalIndicators
import com.example.data.model.Timeframe
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TradingViewChart(
    symbol: String,
    candles: List<Candle>,
    indicators: TechnicalIndicators,
    selectedTimeframe: Timeframe,
    onTimeframeSelected: (Timeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEma by remember { mutableStateOf(true) }
    var showSma by remember { mutableStateOf(false) }
    var showBB by remember { mutableStateOf(true) }
    var showVwap by remember { mutableStateOf(true) }
    var showRsi by remember { mutableStateOf(false) }
    var showMacd by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Chart pan & zoom scale state
    var visibleBarCount by remember { mutableFloatStateOf(40f) }
    var panOffset by remember { mutableFloatStateOf(0f) }
    var touchCrosshairPos by remember { mutableStateOf<Offset?>(null) }

    val activeCandles = candles.ifEmpty {
        listOf(
            Candle(System.currentTimeMillis() - 3600000, 125.0, 128.0, 124.0, 127.5, 5000000L),
            Candle(System.currentTimeMillis(), 127.5, 129.0, 126.8, 128.45, 6200000L)
        )
    }

    val candleCount = activeCandles.size
    val effectiveBarCount = visibleBarCount.coerceIn(10f, candleCount.toFloat())
    val maxPan = max(0f, candleCount - effectiveBarCount)
    val currentPan = panOffset.coerceIn(0f, maxPan)

    val startIndex = (candleCount - effectiveBarCount - currentPan).toInt().coerceIn(0, candleCount - 1)
    val endIndex = (startIndex + effectiveBarCount.toInt()).coerceIn(startIndex + 1, candleCount)
    val visibleCandles = activeCandles.subList(startIndex, endIndex)

    // Calculate dynamic high/low bounds
    val minPrice = visibleCandles.minOfOrNull { it.low } ?: 100.0
    val maxPrice = visibleCandles.maxOfOrNull { it.high } ?: 200.0
    val priceRange = max(1.0, maxPrice - minPrice)

    val maxVolume = visibleCandles.maxOfOrNull { it.volume } ?: 1000000L

    // Hovered candle for crosshair
    val hoveredCandle = remember(touchCrosshairPos, visibleCandles) {
        touchCrosshairPos?.let { pos ->
            val candleWidth = 100f / visibleCandles.size
            val idx = (pos.x / 10f).toInt().coerceIn(0, visibleCandles.size - 1)
            visibleCandles.getOrNull(idx)
        } ?: visibleCandles.lastOrNull()
    }

    CardChartWrapper(
        isFullscreen = isFullscreen,
        modifier = modifier.testTag("tradingview_chart_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header bar: Ticker, OHLC readout, Fullscreen toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedTimeframe.label} CANDLESTICK",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

                    // Live OHLC HUD
                    hoveredCandle?.let { c ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("O: ${String.format("%.2f", c.open)}", fontSize = 11.sp, color = TextMuted)
                            Text("H: ${String.format("%.2f", c.high)}", fontSize = 11.sp, color = BullishGreen)
                            Text("L: ${String.format("%.2f", c.low)}", fontSize = 11.sp, color = BearishRed)
                            Text(
                                "C: ${String.format("%.2f", c.close)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (c.close >= c.open) BullishGreen else BearishRed
                            )
                            Text("V: ${formatVol(c.volume)}", fontSize = 11.sp, color = GoldAccent)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        visibleBarCount = 40f
                        panOffset = 0f
                        touchCrosshairPos = null
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Chart", tint = TextSecondary)
                    }
                    IconButton(onClick = { isFullscreen = !isFullscreen }) {
                        Icon(
                            if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen Toggle",
                            tint = CyanPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timeframe selector bar
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Timeframe.entries.forEach { tf ->
                    val isSelected = tf == selectedTimeframe
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTimeframeSelected(tf) },
                        label = { Text(tf.label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                            selectedLabelColor = CyanPrimary,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Indicator Toggle Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IndicatorChip("EMA", showEma, CyanPrimary) { showEma = !showEma }
                IndicatorChip("SMA", showSma, PurpleAccent) { showSma = !showSma }
                IndicatorChip("BB", showBB, GoldAccent) { showBB = !showBB }
                IndicatorChip("VWAP", showVwap, Color(0xFFFF4081)) { showVwap = !showVwap }
                IndicatorChip("RSI", showRsi, BullishGreen) { showRsi = !showRsi }
                IndicatorChip("MACD", showMacd, Color(0xFF00E5FF)) { showMacd = !showMacd }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Canvas Rendering Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkSurfaceVariant, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, BorderColor, shape = RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            visibleBarCount = (visibleBarCount / zoom).coerceIn(10f, candleCount.toFloat())
                            panOffset = (panOffset - pan.x / 15f).coerceIn(0f, maxPan)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { touchCrosshairPos = it },
                            onDrag = { change, _ -> touchCrosshairPos = change.position },
                            onDragEnd = { touchCrosshairPos = null },
                            onDragCancel = { touchCrosshairPos = null }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val chartHeight = if (showRsi || showMacd) canvasHeight * 0.70f else canvasHeight * 0.85f
                    val subChartTop = chartHeight + 10f
                    val subChartHeight = canvasHeight - subChartTop

                    val candleWidth = canvasWidth / visibleCandles.size
                    val bodyWidth = (candleWidth * 0.65f).coerceAtLeast(3f)

                    // Draw grid lines
                    val gridLines = 5
                    for (g in 0..gridLines) {
                        val y = (chartHeight / gridLines) * g
                        drawLine(
                            color = BorderColor.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                        val priceVal = maxPrice - (g * (priceRange / gridLines))
                        drawIntoCanvas { canvas ->
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#64748B")
                                textSize = 22f
                            }
                            canvas.nativeCanvas.drawText(String.format("%.2f", priceVal), canvasWidth - 110f, y - 5f, paint)
                        }
                    }

                    // Path lines for Bollinger Bands / VWAP / EMA / SMA
                    val bbUpperPath = Path()
                    val bbLowerPath = Path()
                    val ema20Path = Path()
                    val vwapPath = Path()

                    visibleCandles.forEachIndexed { index, candle ->
                        val x = index * candleWidth + (candleWidth / 2f)

                        // Candle price coordinates
                        val openY = chartHeight - (((candle.open - minPrice) / priceRange) * chartHeight).toFloat()
                        val closeY = chartHeight - (((candle.close - minPrice) / priceRange) * chartHeight).toFloat()
                        val highY = chartHeight - (((candle.high - minPrice) / priceRange) * chartHeight).toFloat()
                        val lowY = chartHeight - (((candle.low - minPrice) / priceRange) * chartHeight).toFloat()

                        val isGreen = candle.close >= candle.open
                        val candleColor = if (isGreen) BullishGreen else BearishRed

                        // Wick line
                        drawLine(
                            color = candleColor,
                            start = Offset(x, highY),
                            end = Offset(x, lowY),
                            strokeWidth = 2f
                        )

                        // Body rect
                        val topY = min(openY, closeY)
                        val bottomY = max(openY, closeY)
                        val height = max(3f, abs(bottomY - topY))

                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - (bodyWidth / 2f), topY),
                            size = Size(bodyWidth, height)
                        )

                        // Volume bar at bottom
                        val volHeight = (candle.volume.toFloat() / maxVolume.toFloat()) * (chartHeight * 0.20f)
                        drawRect(
                            color = candleColor.copy(alpha = 0.35f),
                            topLeft = Offset(x - (bodyWidth / 2f), chartHeight - volHeight),
                            size = Size(bodyWidth, volHeight)
                        )

                        // Bollinger Bands & EMA overlays
                        if (showBB) {
                            val bbU = candle.close * 1.025
                            val bbL = candle.close * 0.975
                            val bbUY = chartHeight - (((bbU - minPrice) / priceRange) * chartHeight).toFloat()
                            val bbLY = chartHeight - (((bbL - minPrice) / priceRange) * chartHeight).toFloat()
                            if (index == 0) {
                                bbUpperPath.moveTo(x, bbUY)
                                bbLowerPath.moveTo(x, bbLY)
                            } else {
                                bbUpperPath.lineTo(x, bbUY)
                                bbLowerPath.lineTo(x, bbLY)
                            }
                        }

                        if (showEma) {
                            val emaY = chartHeight - (((indicators.ema20 - minPrice) / priceRange) * chartHeight).toFloat()
                            if (index == 0) ema20Path.moveTo(x, emaY) else ema20Path.lineTo(x, emaY)
                        }

                        if (showVwap) {
                            val vwapY = chartHeight - (((indicators.vwap - minPrice) / priceRange) * chartHeight).toFloat()
                            if (index == 0) vwapPath.moveTo(x, vwapY) else vwapPath.lineTo(x, vwapY)
                        }
                    }

                    if (showBB) {
                        drawPath(bbUpperPath, color = GoldAccent.copy(alpha = 0.7f), style = Stroke(width = 2f))
                        drawPath(bbLowerPath, color = GoldAccent.copy(alpha = 0.7f), style = Stroke(width = 2f))
                    }
                    if (showEma) {
                        drawPath(ema20Path, color = CyanPrimary, style = Stroke(width = 3f))
                    }
                    if (showVwap) {
                        drawPath(vwapPath, color = Color(0xFFFF4081), style = Stroke(width = 2f))
                    }

                    // RSI / MACD Subchart Panel
                    if (showRsi || showMacd) {
                        drawRect(
                            color = DarkSurface,
                            topLeft = Offset(0f, subChartTop),
                            size = Size(canvasWidth, subChartHeight)
                        )

                        if (showRsi) {
                            val rsiY = subChartTop + (subChartHeight * (1.0f - (indicators.rsi.toFloat() / 100f)))
                            drawLine(
                                color = BullishGreen,
                                start = Offset(0f, rsiY),
                                end = Offset(canvasWidth, rsiY),
                                strokeWidth = 2.5f
                            )
                        }
                    }

                    // Crosshair line overlay
                    touchCrosshairPos?.let { pos ->
                        drawLine(
                            color = CyanPrimary,
                            start = Offset(pos.x, 0f),
                            end = Offset(pos.x, canvasHeight),
                            strokeWidth = 1.5f
                        )
                        drawLine(
                            color = CyanPrimary,
                            start = Offset(0f, pos.y),
                            end = Offset(canvasWidth, pos.y),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicatorChip(
    label: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (active) activeColor.copy(alpha = 0.2f) else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) activeColor else BorderColor)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (active) activeColor else TextMuted,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CardChartWrapper(
    isFullscreen: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isFullscreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSurface)
        ) {
            content()
        }
    } else {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
            modifier = modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            content()
        }
    }
}

private fun formatVol(vol: Long): String {
    return when {
        vol >= 1_000_000_000 -> "${String.format("%.1f", vol / 1_000_000_000.0)}B"
        vol >= 1_000_000 -> "${String.format("%.1f", vol / 1_000_000.0)}M"
        vol >= 1_000 -> "${String.format("%.1f", vol / 1_000.0)}K"
        else -> vol.toString()
    }
}
