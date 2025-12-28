package com.example.stream.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stream.data.local.entity.WatchHistoryEntity
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.TvShow
import com.example.stream.ui.components.GlassCard
import com.example.stream.ui.components.MediaPoster
import com.example.stream.ui.components.RatingBadge
import com.example.stream.ui.theme.*
import com.example.stream.util.toPosterUrl
import com.example.stream.util.toBackdropUrl
import com.example.stream.util.rememberSmoothScrollFlingBehavior
import com.example.stream.util.rememberWindowSizeClass
import com.example.stream.util.getVerticalSpacing
import com.example.stream.util.getItemSpacing
import com.example.stream.util.getHorizontalPadding
import com.example.stream.util.toBackdropUrlHQ
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    onPlayMovie: (Int) -> Unit,
    onPlayTvShow: (Int, Int, Int) -> Unit, // tvId, season, episode
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val smoothScrollBehavior = rememberSmoothScrollFlingBehavior()
    val windowSize = rememberWindowSizeClass()
    val verticalSpacing = windowSize.getVerticalSpacing()
    val itemSpacing = windowSize.getItemSpacing()
    val horizontalPadding = windowSize.getHorizontalPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBackground)
    ) {
        if (uiState.isLoading && uiState.trendingMovies.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = GlassPrimary
            )
        } else if (uiState.error != null && uiState.trendingMovies.isEmpty()) {
            ErrorContent(
                error = uiState.error ?: "Unknown error",
                onRetry = { viewModel.retry() },
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                flingBehavior = ScrollableDefaults.flingBehavior() // Smooth fling
            ) {
                // Hero Carousel
                item {
                    HeroCarousel(
                        movies = uiState.trendingMovies.take(5),
                        onMovieClick = onMovieClick
                    )
                }

                // Spacer after hero carousel
                item {
                    Spacer(modifier = Modifier.height(verticalSpacing))
                }

                // Continue Watching Section
                if (uiState.watchHistory.isNotEmpty()) {
                    item {
                        ContinueWatchingSection(
                            watchHistory = uiState.watchHistory,
                            onPlayMovie = onPlayMovie,
                            onPlayTvShow = onPlayTvShow,
                            onRemove = { mediaId, mediaType ->
                                viewModel.removeFromWatchHistory(mediaId, mediaType)
                            }
                        )
                    }
                }

                if (uiState.trendingMovies.isNotEmpty()) {
                    item {
                        MediaSection(
                            title = "Trending Movies",
                            movies = uiState.trendingMovies,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                if (uiState.trendingTvShows.isNotEmpty()) {
                    item {
                        TvShowSection(
                            title = "Trending TV Shows",
                            tvShows = uiState.trendingTvShows,
                            onTvShowClick = onTvShowClick
                        )
                    }
                }

                if (uiState.popularMovies.isNotEmpty()) {
                    item {
                        MediaSection(
                            title = "Popular Movies",
                            movies = uiState.popularMovies,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                if (uiState.nowPlayingMovies.isNotEmpty()) {
                    item {
                        MediaSection(
                            title = "Now Playing",
                            movies = uiState.nowPlayingMovies,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                if (uiState.topRatedMovies.isNotEmpty()) {
                    item {
                        MediaSection(
                            title = "Top Rated Movies",
                            movies = uiState.topRatedMovies,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                if (uiState.topRatedTvShows.isNotEmpty()) {
                    item {
                        TvShowSection(
                            title = "Top Rated TV Shows",
                            tvShows = uiState.topRatedTvShows,
                            onTvShowClick = onTvShowClick
                        )
                    }
                }

                if (uiState.popularAnimes.isNotEmpty()) {
                    item {
                        TvShowSection(
                            title = "Popular Animes",
                            tvShows = uiState.popularAnimes,
                            onTvShowClick = onTvShowClick
                        )
                    }
                }

                if (uiState.topRatedAnimes.isNotEmpty()) {
                    item {
                        TvShowSection(
                            title = "Top Rated Animes",
                            tvShows = uiState.topRatedAnimes,
                            onTvShowClick = onTvShowClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = ScrollableDefaults.flingBehavior() // Smooth horizontal fling
        ) {
            items(
                items = movies,
                key = { it.id } // Add key to prevent recomposition
            ) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
    }
}

@Composable
private fun TvShowSection(
    title: String,
    tvShows: List<TvShow>,
    onTvShowClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = ScrollableDefaults.flingBehavior() // Smooth horizontal fling
        ) {
            items(
                items = tvShows,
                key = { it.id } // Add key to prevent recomposition
            ) { tvShow ->
                TvShowCard(
                    tvShow = tvShow,
                    onClick = { onTvShowClick(tvShow.id) }
                )
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .height(260.dp) // Fixed height to prevent layout shifts
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            MediaPoster(
                imageUrl = movie.posterPath.toPosterUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxWidth()
            )

            RatingBadge(
                rating = movie.voteAverage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TvShowCard(
    tvShow: TvShow,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .height(260.dp) // Fixed height to prevent layout shifts
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            MediaPoster(
                imageUrl = tvShow.posterPath.toPosterUrl(),
                contentDescription = tvShow.name,
                modifier = Modifier.fillMaxWidth()
            )

            RatingBadge(
                rating = tvShow.voteAverage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        Text(
            text = tvShow.name,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge,
                color = AccentRose
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlassPrimary
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroCarousel(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { movies.size }
    )
    
    // Smooth infinite auto-scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // Wait 4 seconds
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 800, // Smooth 800ms transition
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(620.dp) // Taller to extend beyond safe area
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val movie = movies[page]
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onMovieClick(movie.id) }
            ) {
                // Backdrop image - fills entire space with HIGH QUALITY
                MediaPoster(
                    imageUrl = movie.backdropPath.toBackdropUrlHQ(),
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay - extended to cover bottom fully
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Black,
                                    Color.Black
                                )
                            )
                        )
                )

                // Solid black overlay at bottom to ensure full coverage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.Black)
                )

                // Content overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        maxLines = 2,
                        lineHeight = 36.sp
                    )

                    // Genres and Year
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Genre tags (using first 3 genres if available)
                        val genres = listOf("Action", "Adventure", "Sci-Fi") // Placeholder - ideally from movie.genres
                        genres.take(3).forEachIndexed { index, genre ->
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                            if (index < genres.size - 1 && index < 2) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        
                        Text(
                            text = movie.releaseDate.take(4),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // More Info button
                    Button(
                        onClick = { onMovieClick(movie.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .width(140.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "More Info",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(movies.size) { index ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (index == pagerState.currentPage) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .background(
                            color = if (index == pagerState.currentPage) 
                                Color.White 
                            else 
                                Color.White.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
private fun ContinueWatchingSection(
    watchHistory: List<WatchHistoryEntity>,
    onPlayMovie: (Int) -> Unit,
    onPlayTvShow: (Int, Int, Int) -> Unit,
    onRemove: (Int, String) -> Unit = { _, _ -> }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continue Watching",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = ScrollableDefaults.flingBehavior()
        ) {
            items(
                items = watchHistory,
                key = { "${it.mediaId}_${it.mediaType}" } // Unique key for each item
            ) { item ->
                ContinueWatchingCard(
                    item = item,
                    onClick = {
                        if (item.mediaType == "movie") {
                            onPlayMovie(item.mediaId)
                        } else {
                            // Parse episode info (format: "S1E5")
                            val season = item.episodeInfo?.let { info ->
                                info.substringAfter("S").substringBefore("E").toIntOrNull()
                            } ?: 1
                            val episode = item.episodeInfo?.let { info ->
                                info.substringAfter("E").toIntOrNull()
                            } ?: 1
                            
                            onPlayTvShow(item.mediaId, season, episode)
                        }
                    },
                    onRemove = {
                        onRemove(item.mediaId, item.mediaType)
                    }
                )
            }
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    item: WatchHistoryEntity,
    onClick: () -> Unit,
    onRemove: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Backdrop image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.backdropPath.toBackdropUrl())
                .crossfade(true)
                .build(),
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 100f,
                        endY = 500f
                    )
                )
        )

        // Three-dot menu (top-right)
        var showMenu by remember { mutableStateOf(false) }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Remove from Continue Watching")
                    },
                    onClick = {
                        showMenu = false
                        onRemove()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        // Content at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play button
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Title and episode info
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (item.episodeInfo != null && item.episodeInfo.isNotEmpty()) {
                    Text(
                        text = item.episodeInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

