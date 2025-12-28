package com.example.stream.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stream.domain.repository.MovieRepository
import com.example.stream.domain.repository.TvShowRepository
import com.example.stream.domain.repository.WatchlistRepository
import com.example.stream.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    val tvShowRepository: TvShowRepository, // Made public for composable access
    private val watchlistRepository: WatchlistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private val movieId: Int? = savedStateHandle.get<Int>("movieId")
    private val tvId: Int? = savedStateHandle.get<Int>("tvId")

    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()

    init {
        loadDetails()
        observeWatchlistStatus()
    }

    private fun observeWatchlistStatus() {
        viewModelScope.launch {
            val mediaId = movieId ?: tvId ?: return@launch
            val mediaType = if (movieId != null) "movie" else "tv"
            watchlistRepository.isInWatchlist(mediaId, mediaType).collect { inWatchlist ->
                _isInWatchlist.value = inWatchlist
            }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            val mediaId = movieId ?: tvId ?: return@launch
            val mediaType = if (movieId != null) "movie" else "tv"
            val currentState = uiState.value
            
            if (_isInWatchlist.value) {
                // Remove from watchlist
                watchlistRepository.removeFromWatchlist(mediaId, mediaType)
            } else {
                // Add to watchlist
                val title = if (movieId != null) currentState.movie?.title ?: ""
                           else currentState.tvShow?.name ?: ""
                val posterPath = if (movieId != null) currentState.movie?.posterPath ?: ""
                                else currentState.tvShow?.posterPath ?: ""
                val backdropPath = if (movieId != null) currentState.movie?.backdropPath ?: ""
                                  else currentState.tvShow?.backdropPath ?: ""
                val releaseDate = if (movieId != null) currentState.movie?.releaseDate ?: ""
                                 else currentState.tvShow?.firstAirDate ?: ""
                val voteAverage = if (movieId != null) currentState.movie?.voteAverage ?: 0.0
                                 else currentState.tvShow?.voteAverage ?: 0.0
                
                watchlistRepository.addToWatchlist(
                    mediaId = mediaId,
                    mediaType = mediaType,
                    title = title,
                    posterPath = posterPath,
                    backdropPath = backdropPath,
                    releaseDate = releaseDate,
                    voteAverage = voteAverage
                )
            }
        }
    }

    fun retry() {
        loadDetails()
    }

    fun getPersonDetails(personId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPerson = true) }
            
            try {
                // Fetch person details and credits concurrently
                val detailsResult = movieRepository.getPersonDetails(personId)
                val creditsResult = movieRepository.getPersonCredits(personId)
                
                detailsResult.collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            val personDetails = result.data
                            
                            // Fetch credits to get known works
                            creditsResult.collect { creditsRes ->
                                when (creditsRes) {
                                    is NetworkResult.Success -> {
                                        val knownWorks = creditsRes.data ?: emptyList()
                                        _uiState.update {
                                            it.copy(
                                                personDetails = personDetails?.copy(knownFor = knownWorks),
                                                isLoadingPerson = false
                                            )
                                        }
                                    }
                                    is NetworkResult.Error -> {
                                        _uiState.update {
                                            it.copy(
                                                personDetails = personDetails,
                                                isLoadingPerson = false
                                            )
                                        }
                                    }
                                    is NetworkResult.Loading -> {}
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            _uiState.update { it.copy(isLoadingPerson = false) }
                        }
                        is NetworkResult.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingPerson = false) }
            }
        }
    }

    fun clearPersonDetails() {
        _uiState.update { it.copy(personDetails = null) }
    }

    private fun loadDetails() {
        if (movieId != null) {
            loadMovieDetails(movieId)
        } else if (tvId != null) {
            loadTvShowDetails(tvId)
        } else {
            _uiState.update { it.copy(error = "Invalid media ID", isLoading = false) }
        }
    }

    private fun loadMovieDetails(id: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            val result = withTimeoutOrNull(15000) {
                var finalResult: NetworkResult<com.example.stream.domain.model.Movie>? = null
                movieRepository.getMovieDetails(id).collect { networkResult ->
                    if (networkResult !is NetworkResult.Loading) {
                        finalResult = networkResult
                    }
                }
                finalResult
            }
            
            when {
                result == null -> {
                    _uiState.update { it.copy(error = "Request timed out. Please try again.", isLoading = false) }
                }
                result is NetworkResult.Success -> {
                    if (result.data != null) {
                        _uiState.update { it.copy(movie = result.data, isLoading = false, error = null) }
                    } else {
                        _uiState.update { it.copy(error = "Movie not found", isLoading = false) }
                    }
                }
                result is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message ?: "Failed to load movie", isLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(error = "Unknown error", isLoading = false) }
                }
            }
        }

        // Load cast
        viewModelScope.launch {
            withTimeoutOrNull(10000) {
                movieRepository.getMovieCredits(id).collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(cast = result.data ?: emptyList()) }
                    }
                }
            }
        }

        // Load trailers
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTrailers = true) }
            movieRepository.getMovieVideos(id).collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(trailers = result.data ?: emptyList(), isLoadingTrailers = false) }
                } else if (result is NetworkResult.Error) {
                    _uiState.update { it.copy(isLoadingTrailers = false) }
                }
            }
        }

        // Load similar movies
        viewModelScope.launch {
            withTimeoutOrNull(10000) {
                movieRepository.getSimilarMovies(id).collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(similarMovies = result.data ?: emptyList()) }
                    }
                }
            }
        }
    }

    private fun loadTvShowDetails(id: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            val result = withTimeoutOrNull(15000) {
                var finalResult: NetworkResult<com.example.stream.domain.model.TvShow>? = null
                tvShowRepository.getTvShowDetails(id).collect { networkResult ->
                    if (networkResult !is NetworkResult.Loading) {
                        finalResult = networkResult
                    }
                }
                finalResult
            }
            
            when {
                result == null -> {
                    _uiState.update { it.copy(error = "Request timed out. Please try again.", isLoading = false) }
                }
                result is NetworkResult.Success -> {
                    if (result.data != null) {
                        _uiState.update { it.copy(tvShow = result.data, isLoading = false, error = null) }
                    } else {
                        _uiState.update { it.copy(error = "TV show not found", isLoading = false) }
                    }
                }
                result is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message ?: "Failed to load TV show", isLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(error = "Unknown error", isLoading = false) }
                }
            }
        }

        // Load cast
        viewModelScope.launch {
            withTimeoutOrNull(10000) {
                tvShowRepository.getTvShowCredits(id).collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(cast = result.data ?: emptyList()) }
                    }
                }
            }
        }

        // Load trailers
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTrailers = true) }
            tvShowRepository.getTvShowVideos(id).collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(trailers = result.data ?: emptyList(), isLoadingTrailers = false) }
                } else if (result is NetworkResult.Error) {
                    _uiState.update { it.copy(isLoadingTrailers = false) }
                }
            }
        }

        // Load similar TV shows
        viewModelScope.launch {
            withTimeoutOrNull(10000) {
                tvShowRepository.getSimilarTvShows(id).collect { result ->
                    if (result is NetworkResult.Success) {
                        _uiState.update { it.copy(similarTvShows = result.data ?: emptyList()) }
                    }
                }
            }
        }
    }
}
