package com.example.stream.domain.repository

import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.SeasonDetails
import com.example.stream.domain.model.TvShow
import com.example.stream.domain.model.Video
import com.example.stream.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    fun getTrendingTvShows(page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun getPopularTvShows(page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun getTopRatedTvShows(page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun getOnTheAirTvShows(page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun getTvShowDetails(tvId: Int): Flow<NetworkResult<TvShow>>
    fun getTvShowCredits(tvId: Int): Flow<NetworkResult<List<Cast>>>
    fun getSimilarTvShows(tvId: Int, page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun searchTvShows(query: String, page: Int = 1): Flow<NetworkResult<List<TvShow>>>
    fun getSeasonDetails(tvId: Int, seasonNumber: Int): Flow<NetworkResult<SeasonDetails>>
    fun getPopularAnimes(page: Int = 1): Flow<NetworkResult<List<TvShow>>> // Anime content
    fun getTopRatedAnimes(page: Int = 1): Flow<NetworkResult<List<TvShow>>> // Top rated anime
    fun getTvShowVideos(tvId: Int): Flow<NetworkResult<List<Video>>>
}
