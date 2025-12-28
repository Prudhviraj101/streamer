package com.example.stream.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stream.data.local.dao.WatchHistoryDao
import com.example.stream.data.local.dao.WatchlistDao
import com.example.stream.data.local.entity.WatchHistoryEntity
import com.example.stream.data.local.entity.WatchlistEntity

@Database(
    entities = [WatchHistoryEntity::class, WatchlistEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StreamDatabase : RoomDatabase() {
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun watchlistDao(): WatchlistDao
}
