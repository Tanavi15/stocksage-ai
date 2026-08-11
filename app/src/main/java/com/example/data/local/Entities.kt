package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val ticker: String,
    val name: String = ticker,
    val targetAlertHigh: Double? = null,
    val targetAlertLow: Double? = null,
    val notes: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_holdings")
data class PortfolioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticker: String,
    val companyName: String = ticker,
    val shares: Double,
    val buyPrice: Double,
    val notes: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

typealias PortfolioHoldingEntity = PortfolioEntity

@Entity(tableName = "forecast_history")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticker: String,
    val modelType: String = "Monte Carlo 30D",
    val predictedPrice: Double = 0.0,
    val bullCase: Double = 0.0,
    val bearCase: Double = 0.0,
    val confidence: Int = 85,
    val horizon: String = "30 Day",
    val predictedDirection: String = "Bullish",
    val startingPrice: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

typealias ForecastHistoryEntity = ForecastEntity
