package com.example.stream.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stream.domain.repository.MovieRepository
import com.example.stream.domain.repository.TvShowRepository
import com.example.stream.domain.repository.WatchHistoryRepository
import com.example.stream.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val watchHistoryRepository: WatchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeContent()
        loadWatchHistory()
    }

    private fun loadWatchHistory() {
        viewModelScope.launch {
            watchHistoryRepository.getRecentWatchHistory().collect { history ->
                _uiState.update { it.copy(watchHistory = history) }
            }
        }
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load trending movies
            movieRepository.getTrendingMovies().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(trendingMovies = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load trending TV shows
            tvShowRepository.getTrendingTvShows().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(trendingTvShows = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load popular movies
            movieRepository.getPopularMovies().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(popularMovies = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load popular TV shows
            tvShowRepository.getPopularTvShows().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(popularTvShows = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load top rated movies
            movieRepository.getTopRatedMovies().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(topRatedMovies = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load now playing movies
            movieRepository.getNowPlayingMovies().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { 
                            it.copy(nowPlayingMovies = result.data ?: emptyList())
                        }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load top rated TV shows
            tvShowRepository.getTopRatedTvShows().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(topRatedTvShows = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load popular animes
            tvShowRepository.getPopularAnimes().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { it.copy(popularAnimes = result.data ?: emptyList()) }
                    }
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                }
            }
        }

        viewModelScope.launch {
            // Load top rated animes
            tvShowRepository.getTopRatedAnimes().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update { 
                            it.copy(
                                topRatedAnimes = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is NetworkResult.Loading -> {}
                }
            }
        }
    }

    fun retry() {
        loadHomeContent()
    }
    
    fun removeFromWatchHistory(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            watchHistoryRepository.removeFromHistory(mediaId, mediaType)
        }
    }
}
