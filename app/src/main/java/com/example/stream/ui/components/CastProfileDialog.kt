package com.example.stream.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.stream.domain.model.PersonDetails
import com.example.stream.ui.theme.*
import com.example.stream.util.toPosterUrl
import com.example.stream.util.toProfileUrl
import com.example.stream.util.toProfileUrlHQ
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CastProfileDialog(
    personDetails: PersonDetails,
    onDismiss: () -> Unit,
    onWorkClick: (Int, String) -> Unit = { _, _ -> }
) {
    // Animation for dialog entrance
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Scroll state for parallax effect
    val scrollState = rememberScrollState()
    val parallaxOffset = scrollState.value * 0.5f // Parallax factor
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                    slideInVertically(
                        initialOffsetY = { it }, // Start from bottom (full height offset)
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ),
            exit = fadeOut(animationSpec = tween(300)) + 
                   slideOutVertically(
                       targetOffsetY = { it }, // Exit to bottom
                       animationSpec = tween(300)
                   )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                ) {
                    // Parallax background image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .offset(y = (-parallaxOffset).dp) // Parallax effect
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        // High-quality backdrop/Profile image
                        if (personDetails.profilePath != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(personDetails.profilePath.toProfileUrlHQ())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = personDetails.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Enhanced gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.3f),
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f),
                                                Color.Black.copy(alpha = 0.95f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(GlassSurface)
                            )
                        }
                    }

                    // Scrollable content with parallax
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState) // Use the scroll state
                    ) {
                        // Header spacer (for parallax effect)
                        Spacer(modifier = Modifier.height(500.dp))

                        // Content section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .padding(horizontal = 20.dp)
                                .padding(top = 24.dp, bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Personal Info Section
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Personal Info",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                PersonalInfoRow("Known For", personDetails.knownForDepartment)
                                
                                personDetails.birthday?.let {
                                    PersonalInfoRow("Birthday", it)
                                }
                                
                                personDetails.placeOfBirth?.let {
                                    PersonalInfoRow("Place of Birth", it)
                                }
                            }

                            // Biography Section
                            if (personDetails.biography.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "Biography",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = personDetails.biography,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.7f),
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                    )
                                }
                            }

                            // Known For Section
                            if (personDetails.knownFor.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "Known For",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(end = 20.dp),
                                        userScrollEnabled = true
                                    ) {
                                        items(personDetails.knownFor.take(10)) { work ->
                                            KnownWorkCard(
                                                work = work,
                                                onClick = { 
                                                    onWorkClick(work.id, work.mediaType)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Fixed overlay elements (back button and name)
                    // Back button
                    IconButton(
                        onClick = {
                            visible = false
                            scope.launch {
                                delay(300) // Match exit animation duration
                                onDismiss()
                            }
                        },
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

                    // Name at bottom of header (fades out on scroll)
                    val nameAlpha = (1f - (scrollState.value / 300f)).coerceIn(0f, 1f)
                    Text(
                        text = personDetails.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White.copy(alpha = nameAlpha),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(horizontal = 20.dp)
                            .padding(top = 440.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoRow(label: String, value: String) {
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
            color = Color.White
        )
    }
}

@Composable
private fun KnownWorkCard(
    work: com.example.stream.domain.model.KnownWork,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(GlassSurface.copy(alpha = 0.3f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Poster image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(GlassSurfaceVariant)
        ) {
            if (work.posterPath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(work.posterPath.toPosterUrl())
                        .crossfade(true)
                        .build(),
                    contentDescription = work.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Rating badge overlay
            RatingBadge(
                rating = work.voteAverage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            )
        }

        // Title
        Text(
            text = work.title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
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
