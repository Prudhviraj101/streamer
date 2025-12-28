package com.example.stream.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stream.ui.screens.details.DetailsScreen
import com.example.stream.ui.screens.home.HomeScreen
import com.example.stream.ui.screens.library.LibraryScreen
import com.example.stream.ui.screens.player.PlayerScreen
import com.example.stream.ui.screens.search.SearchScreen
import com.example.stream.ui.screens.settings.SettingsScreen
import com.example.stream.ui.theme.*
import com.example.stream.util.rememberHapticFeedback

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StreamNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar on main screens (excluding search)
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Downloads.route,
        Screen.Settings.route
    )

    Scaffold(
        containerColor = GlassBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = {
                    fadeIn(animationSpec = tween(300)) + 
                    slideInHorizontally(animationSpec = tween(300)) { it / 4 }
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300)) + 
                    slideOutHorizontally(animationSpec = tween(300)) { -it / 4 }
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(300)) + 
                    slideInHorizontally(animationSpec = tween(300)) { -it / 4 }
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(300)) + 
                    slideOutHorizontally(animationSpec = tween(300)) { it / 4 }
                }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onMovieClick = { movieId ->
                            navController.navigate(Screen.MovieDetails.createRoute(movieId))
                        },
                        onTvShowClick = { tvId ->
                            navController.navigate(Screen.TvShowDetails.createRoute(tvId))
                        },
                        onPlayMovie = { movieId ->
                            navController.navigate(Screen.MoviePlayer.createRoute(movieId))
                        },
                        onPlayTvShow = { tvId, season, episode ->
                            navController.navigate(Screen.TvPlayer.createRoute(tvId, season, episode))
                        }
                    )
                }

                composable(Screen.Downloads.route) {
                LibraryScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetails.createRoute(movieId))
                    },
                    onTvShowClick = { tvId ->
                        navController.navigate(Screen.TvShowDetails.createRoute(tvId))
                    }
                )
            }
                
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }

                composable(
                    route = Screen.MovieDetails.route,
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it / 2 })
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it / 2 })
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it / 2 })
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it / 2 })
                    }
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                    DetailsScreen(
                        mediaId = movieId,
                        isMovie = true,
                        onBackClick = { navController.popBackStack() },
                        onPlayClick = { navController.navigate(Screen.MoviePlayer.createRoute(movieId)) },
                        onMovieClick = { similarMovieId ->
                            navController.navigate(Screen.MovieDetails.createRoute(similarMovieId))
                        }
                    )
                }

                composable(
                    route = Screen.TvShowDetails.route,
                    arguments = listOf(navArgument("tvId") { type = NavType.IntType }),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it / 2 })
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it / 2 })
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it / 2 })
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it / 2 })
                    }
                ) { backStackEntry ->
                    val tvId = backStackEntry.arguments?.getInt("tvId") ?: return@composable
                    DetailsScreen(
                        mediaId = tvId,
                        isMovie = false,
                        onBackClick = { navController.popBackStack() },
                        onPlayClick = { navController.navigate(Screen.TvPlayer.createRoute(tvId, 1, 1)) },
                        onEpisodePlay = { season, episode ->
                            navController.navigate(Screen.TvPlayer.createRoute(tvId, season, episode))
                        },
                        onTvShowClick = { similarTvId ->
                            navController.navigate(Screen.TvShowDetails.createRoute(similarTvId))
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        onMovieClick = { movieId ->
                            navController.navigate(Screen.MovieDetails.createRoute(movieId))
                        },
                        onTvShowClick = { tvId ->
                            navController.navigate(Screen.TvShowDetails.createRoute(tvId))
                        }
                    )
                }

                // Movie Player
                composable(
                    route = Screen.MoviePlayer.route,
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                    PlayerScreen(
                        mediaId = movieId,
                        mediaType = "movie",
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // TV Show Player
                composable(
                    route = Screen.TvPlayer.route,
                    arguments = listOf(
                        navArgument("tvId") { type = NavType.IntType },
                        navArgument("season") { type = NavType.IntType },
                        navArgument("episode") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val tvId = backStackEntry.arguments?.getInt("tvId") ?: return@composable
                    val season = backStackEntry.arguments?.getInt("season") ?: 1
                    val episode = backStackEntry.arguments?.getInt("episode") ?: 1
                    PlayerScreen(
                        mediaId = tvId,
                        mediaType = "tv",
                        season = season,
                        episode = episode,
                        onBackClick = { navController.popBackStack() },
                        onNextEpisode = { mediaId, newSeason, newEpisode ->
                            navController.navigate(Screen.TvPlayer.createRoute(mediaId, newSeason, newEpisode)) {
                                popUpTo(Screen.TvPlayer.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // Conditionally show bottom bar overlay
            if (showBottomBar) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    BottomNavBar(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val haptic = rememberHapticFeedback()

    // Main navigation items for the Pill
    val mainItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Library", Icons.Default.VideoLibrary, Screen.Downloads.route),
        BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
    )

    // Search item for the Circle
    val searchItem = BottomNavItem("Search", Icons.Default.Search, Screen.Search.route)

    // Floating container for the split navigation
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-10).dp)
            .height(70.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Main Pill (Home, Library, Settings)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .padding(end = 16.dp),
                shape = RoundedCornerShape(50), // Fully rounded pill
                color = Color(0xFF1E1E1E).copy(alpha = 0.9f), // Dark background
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    mainItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        
                        // Icon item
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    haptic.light()
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selected) GlassPrimary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            if (selected) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GlassPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // RIGHT: Search Circle
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(searchItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                shape = CircleShape,
                color = Color(0xFF1E1E1E).copy(alpha = 0.9f), // Dark background
                tonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = searchItem.icon,
                        contentDescription = searchItem.title,
                        tint = if (currentDestination?.hierarchy?.any { it.route == searchItem.route } == true) GlassPrimary else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
