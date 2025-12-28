package com.example.stream.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stream.domain.repository.MovieRepository
import com.example.stream.domain.repository.TvShowRepository
import com.example.stream.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Reduced from 500ms for faster results
                .collectLatest { query ->
                    if (query.length >= 2) { // Reduced from 3 for earlier results
                        searchContent(query)
                    } else if (query.isEmpty()) {
                        _uiState.update { it.copy(movies = emptyList(), tvShows = emptyList(), error = null) }
                    } else {
                        _uiState.update { it.copy(movies = emptyList(), tvShows = emptyList()) }
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun onSearchTypeChange(searchType: SearchType) {
        _uiState.update { it.copy(searchType = searchType) }
        if (_uiState.value.query.length >= 2) {
            searchContent(_uiState.value.query)
        }
    }

    private fun searchContent(query: String) {
        val searchType = _uiState.value.searchType
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val fetchMovies = searchType == SearchType.ALL || searchType == SearchType.MOVIES
                val fetchTv = searchType == SearchType.ALL || searchType == SearchType.TV_SHOWS
                
                val moviesDeferred = if (fetchMovies) async { 
                    movieRepository.searchMovies(query).toList().lastOrNull()
                } else null
                
                val tvDeferred = if (fetchTv) async { 
                    tvShowRepository.searchTvShows(query).toList().lastOrNull() 
                } else null
                
                val movieResult = moviesDeferred?.await()
                val tvShowResult = tvDeferred?.await()
                
                _uiState.update { state ->
                    var newState = state.copy(isLoading = false)
                    
                    if (fetchMovies) {
                        when (movieResult) {
                            is NetworkResult.Success -> {
                                newState = newState.copy(movies = movieResult.data ?: emptyList())
                            }
                            is NetworkResult.Error -> {
                                newState = newState.copy(error = movieResult.message)
                            }
                            else -> {}
                        }
                    }
                    
                    if (fetchTv) {
                        when (tvShowResult) {
                            is NetworkResult.Success -> {
                                newState = newState.copy(tvShows = tvShowResult.data ?: emptyList())
                            }
                            is NetworkResult.Error -> {
                                if (newState.error == null) {
                                    newState = newState.copy(error = tvShowResult.message)
                                }
                            }
                            else -> {}
                        }
                    }
                    
                    newState
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Search failed: ${e.message}"
                ) }
            }
        }
    }
}
