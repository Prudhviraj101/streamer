package com.example.stream.ui.screens.details

import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.PersonDetails
import com.example.stream.domain.model.TvShow
import com.example.stream.domain.model.Video

data class DetailsUiState(
    val movie: Movie? = null,
    val tvShow: TvShow? = null,
    val cast: List<Cast> = emptyList(),
    val similarMovies: List<Movie> = emptyList(),
    val similarTvShows: List<TvShow> = emptyList(),
    val personDetails: PersonDetails? = null, // For cast profile popup
    val trailers: List<Video> = emptyList(), // Trailers
    val isLoading: Boolean = false,
    val isLoadingPerson: Boolean = false, // Separate loading for person details
    val isLoadingTrailers: Boolean = false, // Separate loading for trailers
    val error: String? = null
)
