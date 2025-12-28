package com.example.stream.data.local.dao

import androidx.room.*
import com.example.stream.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC LIMIT 10")
    fun getRecentWatchHistory(): Flow<List<WatchHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: WatchHistoryEntity)
    
    @Query("DELETE FROM watch_history WHERE mediaId = :mediaId AND mediaType = :mediaType")
    suspend fun delete(mediaId: Int, mediaType: String)
    
    @Query("SELECT * FROM watch_history WHERE mediaId = :mediaId AND mediaType = :mediaType LIMIT 1")
    suspend fun getByMediaId(mediaId: Int, mediaType: String): WatchHistoryEntity?
    
    @Query("DELETE FROM watch_history")
    suspend fun clearAll()
}
