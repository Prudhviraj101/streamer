package com.example.stream.domain.repository

import com.example.stream.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepository {
    fun getRecentWatchHistory(): Flow<List<WatchHistoryEntity>>
    
    suspend fun saveWatchProgress(
        mediaId: Int,
        mediaType: String,
        title: String,
        backdropPath: String,
        episodeInfo: String?,
        progress: Float,
        duration: Long
    )
    
    suspend fun removeFromHistory(mediaId: Int, mediaType: String)
    
    suspend fun clearHistory()
}
