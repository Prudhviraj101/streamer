package com.example.stream.ui.screens.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stream.data.local.entity.WatchlistEntity
import com.example.stream.ui.components.MediaPoster
import com.example.stream.ui.theme.GlassBackground
import com.example.stream.ui.theme.GlassPrimary
import com.example.stream.ui.theme.TextPrimary
import com.example.stream.ui.theme.TextSecondary
import com.example.stream.util.rememberWindowSizeClass
import com.example.stream.util.WindowSizeClass
import com.example.stream.util.getGridColumns
import com.example.stream.util.getItemSpacing
import com.example.stream.util.getTitleFontSize
import com.example.stream.util.toPosterUrl

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
    onMovieClick: (Int) -> Unit = {},
    onTvShowClick: (Int) -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Bookmarks", "Downloaded")
    val watchlist by viewModel.watchlist.collectAsState()

    val windowSize = rememberWindowSizeClass()
    val titleSize = windowSize.getTitleFontSize()
    val itemSpacing = windowSize.getItemSpacing()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassBackground)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = titleSize,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Pill-shaped Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color(0xFF2A2A2A), RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (selectedTabIndex == index) Color(0xFF4A4A4A) else Color.Transparent
                        )
                        .clickable { selectedTabIndex = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedTabIndex == index) TextPrimary else TextSecondary
                    )
                }
            }
        }

        
        // Content based on selected tab with crossfade animation
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with
                fadeOut(animationSpec = tween(300))
            },
            label = "tabContent"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> WatchlistSection(
                    watchlist = watchlist,
                    windowSize = windowSize,
                    itemSpacing = itemSpacing,
                    onMovieClick = onMovieClick,
                    onTvShowClick = onTvShowClick
                )
                1 -> DownloadsSection()
            }
        }
    }
}

@Composable
private fun WatchlistSection(
    watchlist: List<WatchlistEntity>,
    windowSize: WindowSizeClass,
    itemSpacing: Dp,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit
) {
    if (watchlist.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your Bookmarks is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Add movies and shows to watch later",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(windowSize.getGridColumns(compact = 2, medium = 3, expanded = 4)),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            items(watchlist) { item ->
                WatchlistCard(
                    item = item,
                    onClick = {
                        if (item.mediaType == "movie") {
                            onMovieClick(item.mediaId)
                        } else {
                            onTvShowClick(item.mediaId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun WatchlistCard(
    item: WatchlistEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MediaPoster(
            imageUrl = item.posterPath.toPosterUrl(),
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        )
        
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = if (item.mediaType == "movie") "Movie" else "TV Show",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun DownloadsSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No Downloads",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Downloaded content will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
