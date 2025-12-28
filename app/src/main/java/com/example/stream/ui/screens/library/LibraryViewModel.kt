package com.example.stream.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stream.data.local.entity.WatchlistEntity
import com.example.stream.domain.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _watchlist = MutableStateFlow<List<WatchlistEntity>>(emptyList())
    val watchlist: StateFlow<List<WatchlistEntity>> = _watchlist.asStateFlow()

    init {
        loadWatchlist()
    }

    private fun loadWatchlist() {
        viewModelScope.launch {
            watchlistRepository.getAllWatchlist().collect { items ->
                _watchlist.value = items
            }
        }
    }
}
