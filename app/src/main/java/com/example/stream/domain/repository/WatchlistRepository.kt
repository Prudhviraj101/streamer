package com.example.stream.domain.repository

import com.example.stream.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>
    
    fun isInWatchlist(mediaId: Int, mediaType: String): Flow<Boolean>
    
    suspend fun addToWatchlist(
        mediaId: Int,
        mediaType: String,
        title: String,
        posterPath: String,
        backdropPath: String,
        releaseDate: String,
        voteAverage: Double
    )
    
    suspend fun removeFromWatchlist(mediaId: Int, mediaType: String)
    
    suspend fun clearWatchlist()
}
