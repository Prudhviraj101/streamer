package com.example.stream.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MovieDetails : Screen("movie/{movieId}") {
        fun createRoute(movieId: Int) = "movie/$movieId"
    }
    object TvShowDetails : Screen("tv/{tvId}") {
        fun createRoute(tvId: Int) = "tv/$tvId"
    }
    object Search : Screen("search")
    object Downloads : Screen("downloads")
    object Settings : Screen("settings")
    object MoviePlayer : Screen("player/movie/{movieId}") {
        fun createRoute(movieId: Int) = "player/movie/$movieId"
    }
    object TvPlayer : Screen("player/tv/{tvId}/{season}/{episode}") {
        fun createRoute(tvId: Int, season: Int = 1, episode: Int = 1) = 
            "player/tv/$tvId/$season/$episode"
    }
}
