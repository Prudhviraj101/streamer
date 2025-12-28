package com.example.stream.ui.screens.player

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository
) : ViewModel() {

    private val _metadata = MutableStateFlow<MediaMetadata?>(null)
    val metadata: StateFlow<MediaMetadata?> = _metadata.asStateFlow()

    /**
     * Fetch media metadata (title, backdrop) from API
     */
    fun fetchMetadata(mediaId: Int, mediaType: String, season: Int = 1, episode: Int = 1) {
        viewModelScope.launch {
            when (mediaType.lowercase()) {
                "movie" -> {
                    movieRepository.getMovieDetails(mediaId).collect { result ->
                        if (result is NetworkResult.Success) {
                            result.data?.let { movie ->
                                _metadata.value = MediaMetadata(
                                    title = movie.title,
                                    backdropPath = movie.backdropPath ?: movie.posterPath ?: "",
                                    episodeInfo = null
                                )
                            }
                        }
                    }
                }
                "tv" -> {
                    tvShowRepository.getTvShowDetails(mediaId).collect { result ->
                        if (result is NetworkResult.Success) {
                            result.data?.let { tvShow ->
                                _metadata.value = MediaMetadata(
                                    title = tvShow.name,
                                    backdropPath = tvShow.backdropPath ?: tvShow.posterPath ?: "",
                                    episodeInfo = "S${season}E${episode}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Save watch progress to database
     */
    fun saveWatchProgress(
        mediaId: Int,
        mediaType: String,
        title: String,
        backdropPath: String,
        episodeInfo: String? = null,
        progress: Float,
        duration: Long
    ) {
        viewModelScope.launch {
            watchHistoryRepository.saveWatchProgress(
                mediaId = mediaId,
                mediaType = mediaType,
                title = title,
                backdropPath = backdropPath,
                episodeInfo = episodeInfo,
                progress = progress,
                duration = duration
            )
        }
    }

    /**
     * Remove item from watch history
     */
    fun removeFromHistory(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            watchHistoryRepository.removeFromHistory(mediaId, mediaType)
        }
    }
}

data class MediaMetadata(
    val title: String,
    val backdropPath: String,
    val episodeInfo: String?
)
