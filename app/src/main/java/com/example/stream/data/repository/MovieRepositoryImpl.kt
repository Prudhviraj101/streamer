package com.example.stream.data.repository

import com.example.stream.data.mapper.toCast
import com.example.stream.data.mapper.toMovie
import com.example.stream.data.mapper.toVideo
import com.example.stream.data.remote.api.TmdbApiService
import com.example.stream.data.remote.dto.toKnownWork
import com.example.stream.data.remote.dto.toPersonDetails
import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.KnownWork
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.PersonDetails
import com.example.stream.domain.model.Video
import com.example.stream.domain.repository.MovieRepository
import com.example.stream.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : MovieRepository {

    override fun getTrendingMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTrendingMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPopularMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getPopularMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTopRatedMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getTopRatedMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getNowPlayingMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getNowPlayingMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<NetworkResult<Movie>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getMovieDetails(movieId)
            emit(NetworkResult.Success(response.toMovie()))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getMovieCredits(movieId: Int): Flow<NetworkResult<List<Cast>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getMovieCredits(movieId)
            emit(NetworkResult.Success(response.cast.map { it.toCast() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getSimilarMovies(movieId: Int, page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getSimilarMovies(movieId, page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.searchMovies(query, page)
            emit(NetworkResult.Success(response.results.map { it.toMovie() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPersonDetails(personId: Int): Flow<NetworkResult<PersonDetails>> = flow {
        emit(NetworkResult.Loading())
        try {
            val personDto = api.getPersonDetails(personId)
            val creditsResponse = api.getPersonCredits(personId)
            val knownWorks = creditsResponse.cast.map { it.toKnownWork() }.take(10)
            emit(NetworkResult.Success(personDto.toPersonDetails(knownWorks)))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPersonCredits(personId: Int): Flow<NetworkResult<List<KnownWork>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getPersonCredits(personId)
            emit(NetworkResult.Success(response.cast.map { it.toKnownWork() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getMovieVideos(movieId: Int): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getMovieVideos(movieId)
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
