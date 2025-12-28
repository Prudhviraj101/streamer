package com.example.stream.data.repository

import com.example.stream.data.local.dao.WatchlistDao
import com.example.stream.data.local.entity.WatchlistEntity
import com.example.stream.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao
) : WatchlistRepository {
    
    override fun getAllWatchlist(): Flow<List<WatchlistEntity>> {
        return watchlistDao.getAllWatchlist()
    }
    
    override fun isInWatchlist(mediaId: Int, mediaType: String): Flow<Boolean> {
        return watchlistDao.isInWatchlist(mediaId, mediaType)
    }
    
    override suspend fun addToWatchlist(
        mediaId: Int,
        mediaType: String,
        title: String,
        posterPath: String,
        backdropPath: String,
        releaseDate: String,
        voteAverage: Double
    ) {
        val entity = WatchlistEntity(
            mediaId = mediaId,
            mediaType = mediaType,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            addedTimestamp = System.currentTimeMillis()
        )
        watchlistDao.insert(entity)
    }
    
    override suspend fun removeFromWatchlist(mediaId: Int, mediaType: String) {
        watchlistDao.delete(mediaId, mediaType)
    }
    
    override suspend fun clearWatchlist() {
        watchlistDao.clearAll()
    }
}
