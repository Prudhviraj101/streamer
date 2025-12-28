package com.example.stream.data.local.dao

import androidx.room.*
import com.example.stream.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedTimestamp DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>
    
    @Query("SELECT * FROM watchlist WHERE mediaId = :mediaId AND mediaType = :mediaType LIMIT 1")
    suspend fun getByMediaId(mediaId: Int, mediaType: String): WatchlistEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)
    
    @Query("DELETE FROM watchlist WHERE mediaId = :mediaId AND mediaType = :mediaType")
    suspend fun delete(mediaId: Int, mediaType: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE mediaId = :mediaId AND mediaType = :mediaType)")
    fun isInWatchlist(mediaId: Int, mediaType: String): Flow<Boolean>
    
    @Query("DELETE FROM watchlist")
    suspend fun clearAll()
}
