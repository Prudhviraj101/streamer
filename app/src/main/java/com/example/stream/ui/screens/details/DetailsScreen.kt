package com.example.stream.ui.screens.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.stream.domain.model.Cast
import com.example.stream.domain.model.Episode
import com.example.stream.domain.model.Movie
import com.example.stream.domain.model.TvShow
import com.example.stream.domain.model.Video
import com.example.stream.domain.repository.TvShowRepository
import com.example.stream.ui.components.CastProfileDialog
import com.example.stream.ui.components.GenreChip
import com.example.stream.ui.components.GlassCard
import com.example.stream.ui.components.MediaPoster
import com.example.stream.ui.components.RatingBadge
import com.example.stream.ui.theme.*
import com.example.stream.util.*

@Composable
fun DetailsScreen(
    mediaId: Int,
    isMovie: Boolean,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit = {},
    onEpisodePlay: (season: Int, episode: Int) -> Unit = { _, _ -> }, // For TV show episodes
    onMovieClick: (Int) -> Unit = {}, // For similar movies navigation
    onTvShowClick: (Int) -> Unit = {}, // For similar TV shows navigation
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    
    val onTrailerClick: (String) -> Unit = { videoKey ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBackground)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = GlassPrimary
                )
            }
            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error ?: "Unknown error",
                    onBackClick = onBackClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.movie != null -> {
                MovieDetailsContent(
                    movie = uiState.movie!!,
                    cast = uiState.cast,
                    similarMovies = uiState.similarMovies,
                    trailers = uiState.trailers,
                    isInWatchlist = isInWatchlist,
                    onBackClick = onBackClick,
                    onPlayClick = { 
                        haptic.medium()
                        onPlayClick()
                    },
                    onWatchlistClick = { 
                        if (isInWatchlist) haptic.light() else haptic.success()
                        viewModel.toggleWatchlist()
                    },
                    onMovieClick = onMovieClick,
                    onCastClick = { personId -> viewModel.getPersonDetails(personId) },
                    onTrailerClick = onTrailerClick
                )
            }
            uiState.tvShow != null -> {
                TvShowDetailsContent(
                    tvShow = uiState.tvShow!!,
                    cast = uiState.cast,
                    similarTvShows = uiState.similarTvShows,
                    trailers = uiState.trailers,
                    isInWatchlist = isInWatchlist,
                    onBackClick = onBackClick,
                    onPlayClick = onPlayClick,
                    onWatchlistClick = { viewModel.toggleWatchlist() },
                    onEpisodePlay = onEpisodePlay,
                    onTvShowClick = onTvShowClick,
                    onCastClick = { personId -> viewModel.getPersonDetails(personId) },
                    onTrailerClick = onTrailerClick
                )
            }
            else -> {
                ErrorContent(
                    error = "Content not found",
                    onBackClick = onBackClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Cast Profile Dialog
        if (uiState.personDetails != null) {
            CastProfileDialog(
                personDetails = uiState.personDetails!!,
                onDismiss = { viewModel.clearPersonDetails() },
                onWorkClick = { workId, mediaType ->
                    // Navigate first, then clear
                    if (mediaType == "movie") {
                        onMovieClick(workId)
                    } else {
                        onTvShowClick(workId)
                    }
                    viewModel.clearPersonDetails()
                }
            )
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: Movie,
    cast: List<Cast>,
    similarMovies: List<Movie>,
    trailers: List<Video>,
    isInWatchlist: Boolean,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onMovieClick: (Int) -> Unit = {},
    onCastClick: (Int) -> Unit = {},
    onTrailerClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val parallaxOffset = scrollState.value * 0.5f
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop image with parallax
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .offset(y = (-parallaxOffset).dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.backdropPath.toBackdropUrlHQ())
                    .crossfade(true)
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f),
                                Color.Black
                            )
                        )
                    )
            )
        }
        
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Spacer for backdrop
            Spacer(modifier = Modifier.height(420.dp))
            
            // Play and Add buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play button
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A2A).copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Watchlist button
                IconButton(
                    onClick = onWatchlistClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFF2A2A2A).copy(alpha = 0.85f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Download button
                IconButton(
                    onClick = { /* Download movie */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFF2A2A2A).copy(alpha = 0.85f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title and metadata
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                if (movie.tagline.isNotEmpty()) {
                    Text(
                        text = movie.tagline,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Overview Section
            if (movie.overview.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Information Section
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (movie.releaseDate.isNotEmpty()) {
                    InfoRowNetflix("Release Date", movie.releaseDate)
                }
                if (movie.runtime != null && movie.runtime > 0) {
                    InfoRowNetflix("Runtime", movie.runtime.toRuntimeString())
                }
                InfoRowNetflix("Rating", "${movie.voteAverage.toRatingString()}/10")
                
                if (movie.genres.isNotEmpty()) {
                    InfoRowNetflix("Genres", movie.genres.joinToString(", ") { it.name })
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Trailers Section
            if (trailers.isNotEmpty()) {
                TrailersSection(trailers = trailers, onTrailerClick = onTrailerClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Cast Section
            if (cast.isNotEmpty()) {
                CastSection(cast = cast, onCastClick = onCastClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Similar Movies
            if (similarMovies.isNotEmpty()) {
                SimilarMoviesSection(movies = similarMovies, onMovieClick = onMovieClick)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // Back button overlay
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun InfoRowNetflix(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TvShowDetailsContent(
    tvShow: TvShow,
    cast: List<Cast>,
    similarTvShows: List<TvShow>,
    trailers: List<Video>,
    isInWatchlist: Boolean,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onEpisodePlay: (season: Int, episode: Int) -> Unit,
    onTvShowClick: (Int) -> Unit = {},
    onCastClick: (Int) -> Unit = {},
    onTrailerClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val parallaxOffset = scrollState.value * 0.5f
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop image with parallax
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .offset(y = (-parallaxOffset).dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(tvShow.backdropPath.toBackdropUrlHQ())
                    .crossfade(true)
                    .build(),
                contentDescription = tvShow.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f),
                                Color.Black
                            )
                        )
                    )
            )
        }
        
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Spacer for backdrop
            Spacer(modifier = Modifier.height(420.dp))
            
            // Play and Add buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play button
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A2A).copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Watchlist button
                IconButton(
                    onClick = onWatchlistClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFF2A2A2A).copy(alpha = 0.85f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title and metadata
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tvShow.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                if (tvShow.tagline.isNotEmpty()) {
                    Text(
                        text = tvShow.tagline,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Overview Section
            if (tvShow.overview.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tvShow.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Information Section
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (tvShow.firstAirDate.isNotEmpty()) {
                    InfoRowNetflix("First Air Date", tvShow.firstAirDate)
                }
                if (tvShow.numberOfSeasons != null && tvShow.numberOfSeasons > 0) {
                    InfoRowNetflix("Seasons", tvShow.numberOfSeasons.toString())
                }
                if (tvShow.numberOfEpisodes != null && tvShow.numberOfEpisodes > 0) {
                    InfoRowNetflix("Episodes", tvShow.numberOfEpisodes.toString())
                }
                InfoRowNetflix("Rating", "${tvShow.voteAverage.toRatingString()}/10")
                
                if (tvShow.genres.isNotEmpty()) {
                    InfoRowNetflix("Genres", tvShow.genres.joinToString(", ") { it.name })
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Seasons Section
            if (tvShow.numberOfSeasons != null && tvShow.numberOfSeasons > 0) {
                SeasonsSection(
                    tvShowId = tvShow.id,
                    numberOfSeasons = tvShow.numberOfSeasons,
                    onEpisodeClick = { seasonNumber, episodeNumber ->
                        onEpisodePlay(seasonNumber, episodeNumber)
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Trailers Section
            if (trailers.isNotEmpty()) {
                TrailersSection(trailers = trailers, onTrailerClick = onTrailerClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Cast Section
            if (cast.isNotEmpty()) {
                CastSection(cast = cast, onCastClick = onCastClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Similar TV Shows
            if (similarTvShows.isNotEmpty()) {
                SimilarTvShowsSection(tvShows = similarTvShows, onTvShowClick = onTvShowClick)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // Back button overlay
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun HeroSection(
    backdropUrl: String,
    title: String,
    rating: Double,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        if (backdropUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(backdropUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .gradientBackground()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayDark)
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        if (rating > 0) {
            RatingBadge(
                rating = rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GlassPrimary
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Play")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun CastSection(
    cast: List<Cast>,
    onCastClick: (Int) -> Unit = {}
) {
    val validCast = cast.filter { it.profilePath.isNotEmpty() }.take(10)
    
    if (validCast.isEmpty()) return
    
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cast",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(validCast) { castMember ->
                CastCard(
                    cast = castMember,
                    onClick = { onCastClick(castMember.id) }
                )
            }
        }
    }
}

@Composable
private fun CastCard(
    cast: Cast,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cast.profilePath.toProfileUrl())
                .crossfade(true)
                .build(),
            contentDescription = cast.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(GlassSurfaceVariant),
            contentScale = ContentScale.Crop
        )

        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            maxLines = 2
        )

        if (cast.character.isNotEmpty()) {
            Text(
                text = cast.character,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SimilarMoviesSection(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Similar Movies",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies.take(10)) { movie ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onMovieClick(movie.id) }, // Make clickable
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MediaPoster(
                        imageUrl = movie.posterPath.toPosterUrl(),
                        contentDescription = movie.title
                    )
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun SimilarTvShowsSection(
    tvShows: List<TvShow>,
    onTvShowClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Similar TV Shows",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tvShows.take(10)) { tvShow ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onTvShowClick(tvShow.id) }, // Make clickable
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MediaPoster(
                        imageUrl = tvShow.posterPath.toPosterUrl(),
                        contentDescription = tvShow.name
                    )
                    Text(
                        text = tvShow.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun TrailersSection(
    trailers: List<Video>,
    onTrailerClick: (String) -> Unit
) {
    if (trailers.isEmpty()) return
    
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Trailers",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trailers.take(5)) { video ->
                TrailerCard(
                    video = video,
                    onClick = { onTrailerClick(video.key) }
                )
            }
        }
    }
}

@Composable
private fun TrailerCard(
    video: Video,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://img.youtube.com/vi/${video.key}/hqdefault.jpg",
                contentDescription = video.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            
            // Play Icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(Color(0xFF2A2A2A).copy(alpha = 0.85f), CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Trailer",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Name
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = video.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
private fun ErrorContent(
    error: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCard {
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
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassPrimary
                    )
                ) {
                    Text("Go Back")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonsSection(
    tvShowId: Int,
    numberOfSeasons: Int,
    onEpisodeClick: (seasonNumber: Int, episodeNumber: Int) -> Unit
) {
    var selectedSeason by remember { mutableStateOf(1) }
    var expanded by remember { mutableStateOf(false) }
    var episodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val tvShowRepository: TvShowRepository = hiltViewModel<DetailsViewModel>().tvShowRepository
    
    // Fetch episodes when season changes
    LaunchedEffect(selectedSeason) {
        isLoading = true
        tvShowRepository.getSeasonDetails(tvShowId, selectedSeason).collect { result ->
            when (result) {
                is NetworkResult.Success -> {
                    episodes = result.data?.episodes ?: emptyList()
                    isLoading = false
                }
                is NetworkResult.Error -> {
                    isLoading = false
                }
                is NetworkResult.Loading -> {
                    isLoading = true
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Seasons",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        
        // Season Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "Season $selectedSeason",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GlassPrimary,
                    unfocusedBorderColor = GlassSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                repeat(numberOfSeasons) { index ->
                    DropdownMenuItem(
                        text = { Text("Season ${index + 1}") },
                        onClick = {
                            selectedSeason = index + 1
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Episodes List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GlassPrimary)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top, // Ensure top alignment
                flingBehavior = ScrollableDefaults.flingBehavior()
            ) {
                items(episodes) { episode ->
                    EpisodeCard(
                        episode = episode,
                        onClick = { onEpisodeClick(selectedSeason, episode.episodeNumber) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    GlassCard(
        modifier = Modifier
            .width(240.dp)
            .height(240.dp) // Fixed height for perfect alignment
            .clickable { onClick() } // Make card clickable
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Episode thumbnail with real image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp) // Compact 16:9 aspect ratio
                    .clip(MaterialTheme.shapes.small)
                    .background(GlassSurface)
            ) {
                if (episode.stillPath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(episode.stillPath.toBackdropUrl())
                            .crossfade(true)
                            .build(),
                        contentDescription = episode.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Play icon overlay (center)
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
                
                // Download button overlay (bottom-right)
                IconButton(
                    onClick = { /* Download episode */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(
                            Color(0xFF2A2A2A).copy(alpha = 0.85f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download episode",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // Episode info
            Column(
                modifier = Modifier.padding(10.dp), // Reduced padding
                verticalArrangement = Arrangement.spacedBy(4.dp) // Tighter spacing
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EPISODE ${episode.episodeNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    episode.runtime?.let {
                        Text(
                            text = "⏱ ${it}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
        }
    }
}
