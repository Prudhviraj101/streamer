package com.example.stream.data.repository

import com.example.stream.data.mapper.toCast
import com.example.stream.data.mapper.toTvShow
import com.example.stream.data.mapper.toVideo
import com.example.stream.data.remote.api.TmdbApiService
import com.example.stream.data.remote.dto.toSeasonDetails
import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.SeasonDetails
import com.example.stream.domain.model.TvShow
import com.example.stream.domain.model.Video
import com.example.stream.domain.repository.TvShowRepository
import com.example.stream.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TvShowRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : TvShowRepository {

    override fun getTrendingTvShows(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTrendingTvShows(page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPopularTvShows(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getPopularTvShows(page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTopRatedTvShows(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTopRatedTvShows(page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getOnTheAirTvShows(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getOnTheAirTvShows(page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTvShowDetails(tvId: Int): Flow<NetworkResult<TvShow>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTvShowDetails(tvId)
            emit(NetworkResult.Success(response.toTvShow()))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTvShowCredits(tvId: Int): Flow<NetworkResult<List<Cast>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTvShowCredits(tvId)
            emit(NetworkResult.Success(response.cast.map { it.toCast() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getSimilarTvShows(tvId: Int, page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getSimilarTvShows(tvId, page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchTvShows(query: String, page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.searchTvShows(query, page)
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getSeasonDetails(tvId: Int, seasonNumber: Int): Flow<NetworkResult<SeasonDetails>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getSeasonDetails(tvId, seasonNumber)
            emit(NetworkResult.Success(response.toSeasonDetails()))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPopularAnimes(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            // Fetch TV shows with Animation genre (16) and Japanese origin (JP)
            val response = api.discoverTvShows(
                genres = "16", // Animation genre
                originCountry = "JP", // Japanese
                sortBy = "popularity.desc",
                page = page
            )
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTopRatedAnimes(page: Int): Flow<NetworkResult<List<TvShow>>> = flow {
        emit(NetworkResult.Loading())
        try {
            // Fetch top rated TV shows with Animation genre (16) and Japanese origin (JP)
            val response = api.discoverTvShows(
                genres = "16", // Animation genre
                originCountry = "JP", // Japanese
                sortBy = "vote_average.desc",
                page = page
            )
            emit(NetworkResult.Success(response.results.map { it.toTvShow() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTvShowVideos(tvId: Int): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTvShowVideos(tvId)
            val videos = response.results
                .filter { it.site == "YouTube" }
                .map { it.toVideo() }
                .sortedByDescending { it.official } // Official videos first
            emit(NetworkResult.Success(videos))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Failed to fetch videos"))
        }
    }
}
