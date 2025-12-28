package com.example.stream.ui.screens.search

import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.TvShow

data class SearchUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val tvShows: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchType: SearchType = SearchType.ALL
)

enum class SearchType {
    ALL, MOVIES, TV_SHOWS
}
