package com.example.stream.ui.screens.home

import com.example.stream.data.local.entity.WatchHistoryEntity
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.TvShow

data class HomeUiState(
    val trendingMovies: List<Movie> = emptyList(),
    val trendingTvShows: List<TvShow> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val popularTvShows: List<TvShow> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val topRatedTvShows: List<TvShow> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val popularAnimes: List<TvShow> = emptyList(), // Anime content (Animation genre + Japanese)
    val topRatedAnimes: List<TvShow> = emptyList(), // Top rated anime
    val watchHistory: List<WatchHistoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
