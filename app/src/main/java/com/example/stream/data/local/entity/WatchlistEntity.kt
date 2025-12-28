package com.example.stream.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist",
    primaryKeys = ["mediaId", "mediaType"]
)
data class WatchlistEntity(
    val mediaId: Int,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val addedTimestamp: Long = System.currentTimeMillis()
)
