package com.example.stream.data.repository

import com.example.stream.data.local.dao.WatchHistoryDao
import com.example.stream.data.local.entity.WatchHistoryEntity
import com.example.stream.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchHistoryRepositoryImpl @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao
) : WatchHistoryRepository {
    
    override fun getRecentWatchHistory(): Flow<List<WatchHistoryEntity>> {
        return watchHistoryDao.getRecentWatchHistory()
    }
    
    override suspend fun saveWatchProgress(
        mediaId: Int,
        mediaType: String,
        title: String,
        backdropPath: String,
        episodeInfo: String?,
        progress: Float,
        duration: Long
    ) {
        // If content is completed (95% or more), remove from history
        if (progress >= 0.95f) {
            watchHistoryDao.delete(mediaId, mediaType)
            return
        }
        
        // Otherwise, save/update the watch progress
        val entity = WatchHistoryEntity(
            mediaId = mediaId,
            mediaType = mediaType,
            title = title,
            backdropPath = backdropPath,
            episodeInfo = episodeInfo,
            progress = progress,
            duration = duration,
            lastWatchedTimestamp = System.currentTimeMillis()
        )
        watchHistoryDao.insertOrUpdate(entity)
    }
    
    override suspend fun removeFromHistory(mediaId: Int, mediaType: String) {
        watchHistoryDao.delete(mediaId, mediaType)
    }
    
    override suspend fun clearHistory() {
        watchHistoryDao.clearAll()
    }
}
