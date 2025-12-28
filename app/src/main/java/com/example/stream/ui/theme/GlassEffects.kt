package com.example.stream.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glass morphism effect modifier
 */
fun Modifier.glassMorphism(
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 8.dp
): Modifier = this
    .shadow(
        elevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = Color.White.copy(alpha = 0.1f),
        spotColor = Color.White.copy(alpha = 0.1f)
    )
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                GlassSurface,
                GlassSurfaceVariant
            )
        )
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.1f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

/**
 * Gradient background modifier
 */
fun Modifier.gradientBackground(
    colors: List<Color> = listOf(GlassPrimary, GlassSecondary, GlassAccent),
    angle: Float = 45f
): Modifier = this.background(
    brush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )
)

/**
 * Shimmer effect for loading states
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                ShimmerBase,
                ShimmerHighlight,
                ShimmerBase
            ),
            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
            end = Offset(translateAnim, translateAnim)
        )
    )
}

/**
 * Pulsing animation for attention-grabbing elements
 */
fun Modifier.pulseAnimation(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    this
}
