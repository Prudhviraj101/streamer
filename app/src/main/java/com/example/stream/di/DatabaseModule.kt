package com.example.stream.di

import android.content.Context
import androidx.room.Room
import com.example.stream.data.local.StreamDatabase
import com.example.stream.data.local.dao.WatchHistoryDao
import com.example.stream.data.local.dao.WatchlistDao
import com.example.stream.data.repository.WatchHistoryRepositoryImpl
import com.example.stream.data.repository.WatchlistRepositoryImpl
import com.example.stream.domain.repository.WatchHistoryRepository
import com.example.stream.domain.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideStreamDatabase(@ApplicationContext context: Context): StreamDatabase {
        return Room.databaseBuilder(
            context,
            StreamDatabase::class.java,
            "stream_database"
        )
        .fallbackToDestructiveMigration() // For development - recreates DB on version change
        .build()
    }
    
    @Provides
    @Singleton
    fun provideWatchHistoryDao(database: StreamDatabase): WatchHistoryDao {
        return database.watchHistoryDao()
    }
    
    @Provides
    @Singleton
    fun provideWatchlistDao(database: StreamDatabase): WatchlistDao {
        return database.watchlistDao()
    }
    
    @Provides
    @Singleton
    fun provideWatchHistoryRepository(dao: WatchHistoryDao): WatchHistoryRepository {
        return WatchHistoryRepositoryImpl(dao)
    }
    
    @Provides
    @Singleton
    fun provideWatchlistRepository(dao: WatchlistDao): WatchlistRepository {
        return WatchlistRepositoryImpl(dao)
    }
}
