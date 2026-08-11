package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    suspend fun getAllWatchlist(): List<WatchlistEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE ticker = :ticker)")
    suspend fun isTickerInWatchlist(ticker: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE ticker = :ticker")
    suspend fun deleteWatchlist(ticker: String)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_holdings ORDER BY addedAt DESC")
    suspend fun getAllPortfolio(): List<PortfolioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(item: PortfolioEntity)

    @Query("DELETE FROM portfolio_holdings WHERE ticker = :ticker")
    suspend fun deletePosition(ticker: String)
}

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast_history ORDER BY timestamp DESC")
    suspend fun getForecastHistory(): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(item: ForecastEntity)
}

@Database(
    entities = [WatchlistEntity::class, PortfolioEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockSageDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: StockSageDatabase? = null

        fun getDatabase(context: Context): StockSageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockSageDatabase::class.java,
                    "stocksage_x_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
