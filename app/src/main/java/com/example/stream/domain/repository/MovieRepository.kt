package com.example.stream.domain.repository

import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.KnownWork
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.PersonDetails
import com.example.stream.domain.model.Video
import com.example.stream.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun getPopularMovies(page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun getTopRatedMovies(page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun getNowPlayingMovies(page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<NetworkResult<Movie>>
    fun getMovieCredits(movieId: Int): Flow<NetworkResult<List<Cast>>>
    fun getSimilarMovies(movieId: Int, page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun searchMovies(query: String, page: Int = 1): Flow<NetworkResult<List<Movie>>>
    fun getPersonDetails(personId: Int): Flow<NetworkResult<PersonDetails>>
    fun getPersonCredits(personId: Int): Flow<NetworkResult<List<KnownWork>>>
    fun getMovieVideos(movieId: Int): Flow<NetworkResult<List<Video>>>
}
