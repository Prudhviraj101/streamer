package com.example.stream.util

object Constants {
    // TMDB API
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    
    // Image Sizes
    const val IMAGE_SIZE_W500 = "w500"
    const val IMAGE_SIZE_W780 = "w780"
    const val IMAGE_SIZE_W1280 = "w1280"
    const val IMAGE_SIZE_ORIGINAL = "original"
    const val IMAGE_SIZE_W185 = "w185"
    const val IMAGE_SIZE_W342 = "w342"
    
    // Pagination
    const val ITEMS_PER_PAGE = 20
    const val INITIAL_PAGE = 1
    
    // Database
    const val DATABASE_NAME = "stream_database"
    
    // DataStore
    const val PREFERENCES_NAME = "stream_preferences"
    
    // Network
    const val NETWORK_TIMEOUT = 30L
    
    // Media Types
    const val MEDIA_TYPE_MOVIE = "movie"
    const val MEDIA_TYPE_TV = "tv"
}
