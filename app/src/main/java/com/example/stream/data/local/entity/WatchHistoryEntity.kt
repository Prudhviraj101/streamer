package com.example.stream.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey
    val mediaId: Int,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val backdropPath: String,
    val episodeInfo: String? = null, // "S1E1" for TV shows
    val progress: Float = 0f, // 0.0 to 1.0
    val duration: Long = 0, // Total duration in milliseconds
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)
