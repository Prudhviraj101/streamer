package com.example.stream.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stream.ui.components.MediaPoster
import com.example.stream.ui.theme.*
import com.example.stream.util.toPosterUrl
import com.example.stream.util.rememberWindowSizeClass
import com.example.stream.util.WindowSizeClass
import com.example.stream.util.getVerticalSpacing
import com.example.stream.util.getHorizontalPadding
import com.example.stream.util.getItemSpacing

@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val windowSize = rememberWindowSizeClass()
    val verticalSpacing = windowSize.getVerticalSpacing()
    val horizontalPadding = windowSize.getHorizontalPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBackground)
    ) {
        // Results content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Only bottom padding for search bar
                .imePadding() // Adjust for keyboard
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GlassPrimary)
                    }
                }
                uiState.query.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search for movies and TV shows",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
                uiState.movies.isEmpty() && uiState.tvShows.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = horizontalPadding,
                            end = horizontalPadding,
                            top = horizontalPadding,
                            bottom = verticalSpacing
                        ),
                        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                    ) {
                        // Movies section
                        if (uiState.movies.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Movies",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                            }
                            items(
                                items = uiState.movies,
                                key = { it.id }
                            ) { movie ->
                                SearchResultItem(
                                    title = movie.title,
                                    subtitle = "${movie.releaseDate.take(4)} • Movie",
                                    posterPath = movie.posterPath,
                                    onClick = { onMovieClick(movie.id) }
                                )
                            }
                        }

                        // TV Shows section
                        if (uiState.tvShows.isNotEmpty()) {
                            item {
                                Text(
                                    text = "TV Shows",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                            }
                            items(
                                items = uiState.tvShows,
                                key = { it.id }
                            ) { tvShow ->
                                SearchResultItem(
                                    title = tvShow.name,
                                    subtitle = "${tvShow.firstAirDate.take(4)} • TV Show",
                                    posterPath = tvShow.posterPath,
                                    onClick = { onTvShowClick(tvShow.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search bar at bottom (floating) - Responsive
        val searchBarPadding = when (windowSize) {
            WindowSizeClass.Compact -> 12.dp
            WindowSizeClass.Medium -> 16.dp
            WindowSizeClass.Expanded -> 20.dp
        }
        val searchBarHeight = when (windowSize) {
            WindowSizeClass.Compact -> 56.dp
            WindowSizeClass.Medium -> 60.dp
            WindowSizeClass.Expanded -> 64.dp
        }
        val buttonSize = when (windowSize) {
            WindowSizeClass.Compact -> 48.dp
            WindowSizeClass.Medium -> 52.dp
            WindowSizeClass.Expanded -> 56.dp
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = searchBarPadding, vertical = searchBarPadding)
                .imePadding(), // Move above keyboard
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search field
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .height(searchBarHeight),
                placeholder = { 
                    Text(
                        "Search movies and TV shows...",
                        color = Color.Gray
                    ) 
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = GlassPrimary
                ),
                singleLine = true
            )

            // Close button
            IconButton(
                onClick = { viewModel.onQueryChange("") },
                modifier = Modifier
                    .size(buttonSize)
                    .background(Color(0xFF2A2A2A), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    title: String,
    posterPath: String?,
    subtitle: String,
    onClick: () -> Unit
) {
    val windowSize = rememberWindowSizeClass()
    val posterWidth = when (windowSize) {
        WindowSizeClass.Compact -> 50.dp
        WindowSizeClass.Medium -> 60.dp
        WindowSizeClass.Expanded -> 70.dp
    }
    val spacing = windowSize.getItemSpacing()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing, vertical = spacing / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MediaPoster(
            imageUrl = posterPath.toPosterUrl(),
            contentDescription = title,
            modifier = Modifier
                .width(posterWidth)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(spacing))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
