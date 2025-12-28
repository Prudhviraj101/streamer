package com.example.stream.ui.screens.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.mutableFloatStateOf
import android.provider.Settings
import android.media.AudioManager
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import com.example.stream.ui.theme.GlassBackground
import com.example.stream.ui.theme.GlassPrimary
import com.example.stream.ui.theme.TextPrimary
import com.example.stream.util.rememberWindowSizeClass
import com.example.stream.util.getButtonHeight
import com.example.stream.util.getIconSize

// Extension function to find activity from context
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun PlayerScreen(
    mediaId: Int,
    mediaType: String, // "movie" or "tv"
    season: Int = 1,
    episode: Int = 1,
    onBackClick: () -> Unit,
    onNextEpisode: (Int, Int, Int) -> Unit = { _, _, _ -> }, // mediaId, season, episode
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val metadata by viewModel.metadata.collectAsState()
    
    // Brightness and volume state
    var brightness by remember { mutableFloatStateOf(0.5f) }
    var volume by remember { mutableFloatStateOf(0.5f) }
    var showBrightnessIndicator by remember { mutableStateOf(false) }
    var showVolumeIndicator by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    
    val windowSize = rememberWindowSizeClass()
    val buttonHeight = windowSize.getButtonHeight()
    val iconSize = windowSize.getIconSize()
    
    // Audio manager for volume control
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    
    // Initialize current volume
    LaunchedEffect(Unit) {
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
    }
    
    // Hide indicators after delay
    LaunchedEffect(showBrightnessIndicator) {
        if (showBrightnessIndicator) {
            delay(1000)
            showBrightnessIndicator = false
        }
    }
    
    LaunchedEffect(showVolumeIndicator) {
        if (showVolumeIndicator) {
            delay(1000)
            showVolumeIndicator = false
        }
    }
    
    // Auto-hide controls after 2 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(2000)
            showControls = false
        }
    }
    
    // Fetch media metadata when player loads
    LaunchedEffect(mediaId, mediaType, season, episode) {
        viewModel.fetchMetadata(mediaId, mediaType, season, episode)
    }
    
    // Save watch progress when metadata is available
    LaunchedEffect(metadata) {
        metadata?.let { meta ->
            viewModel.saveWatchProgress(
                mediaId = mediaId,
                mediaType = mediaType,
                title = meta.title,
                backdropPath = meta.backdropPath,
                episodeInfo = meta.episodeInfo,
                progress = 0.0f,
                duration = 0L
            )
        }
    }
    
    // Lock orientation to landscape when this screen is active
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        onDispose {
            // Restore original orientation when leaving player
            activity?.requestedOrientation = originalOrientation ?: android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    
    // Build Videasy player URL (official documentation)
    val playerUrl = remember(mediaId, mediaType, season, episode) {
        val baseUrl = when (mediaType.lowercase()) {
            "movie" -> "https://player.videasy.net/movie/$mediaId"
            "tv" -> "https://player.videasy.net/tv/$mediaId/$season/$episode"
            else -> "https://player.videasy.net/movie/$mediaId"
        }
        // Disable overlay and enable autoplay to remove play button
        val finalUrl = "$baseUrl?color=6366F1&overlay=false&autoplay=true"
        Log.d("PlayerScreen", "Loading Videasy URL: $finalUrl")
        finalUrl
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // WebView Player
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(false)
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                        allowContentAccess = true
                        databaseEnabled = true
                        javaScriptCanOpenWindowsAutomatically = true
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        
                        // Enable caching to reduce DNS lookups and improve loading
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        
                        // Network optimization
                        blockNetworkImage = false
                        loadsImagesAutomatically = true
                        
                        // Disable safe browsing to avoid DNS lookups (API 26+)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            safeBrowsingEnabled = false
                        }
                    }
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            Log.d("PlayerScreen", "Page finished loading: $url")
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            if (request?.isForMainFrame == true) {
                                hasError = true
                                errorMessage = error?.description?.toString() ?: "Failed to load player"
                                isLoading = false
                                Log.e("PlayerScreen", "WebView error: $errorMessage")
                            }
                        }
                    }
                    
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            Log.d("PlayerScreen", "Loading progress: $newProgress%")
                            if (newProgress >= 80) {
                                isLoading = false
                            }
                        }

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("PlayerScreen", "Console: ${consoleMessage?.message()}")
                            return true
                        }
                    }
                    
                    loadUrl(playerUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Right-side tap area for Next Episode controls (doesn't block center play button)
        Box(
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showControls = !showControls
                        }
                    )
                }
        )
        
        // Brightness control - Left edge only
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(80.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        val delta = -dragAmount / size.height
                        brightness = (brightness + delta).coerceIn(0f, 1f)
                        
                        // Apply brightness to window
                        val activity = context.findActivity()
                        activity?.window?.attributes = activity?.window?.attributes?.apply {
                            screenBrightness = brightness
                        }
                        
                        showBrightnessIndicator = true
                    }
                }
        )
        
        // Volume control - Right edge only
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(80.dp)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        val delta = -dragAmount / size.height
                        volume = (volume + delta).coerceIn(0f, 1f)
                        
                        // Apply volume
                        val newVolume = (volume * maxVolume).toInt()
                        audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            newVolume,
                            0
                        )
                        
                        showVolumeIndicator = true
                    }
                }
        )
        
        // Next Episode button (only for TV shows and when controls are visible)
        if (mediaType.lowercase() == "tv" && showControls) {
            Button(
                onClick = {
                    // Navigate to next episode
                    val nextEpisode = episode + 1
                    onNextEpisode(mediaId, season, nextEpisode)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Episode",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Next Episode",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        // Brightness indicator
        if (showBrightnessIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 32.dp)
                    .width(60.dp)
                    .height(200.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Brightness6,
                        contentDescription = "Brightness",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    // Vertical progress bar
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(120.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(brightness)
                                .background(Color.White, RoundedCornerShape(2.dp))
                                .align(Alignment.BottomCenter)
                        )
                    }
                    
                    Text(
                        text = "${(brightness * 100).toInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Volume indicator
        if (showVolumeIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 32.dp)
                    .width(60.dp)
                    .height(200.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    // Vertical progress bar
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(120.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(volume)
                                .background(Color.White, RoundedCornerShape(2.dp))
                                .align(Alignment.BottomCenter)
                        )
                    }
                    
                    Text(
                        text = "${(volume * 100).toInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Error State
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Failed to load player",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary.copy(alpha = 0.7f)
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

        // Loading Indicator
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = GlassPrimary)
                    Text(
                        text = "Loading player...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
