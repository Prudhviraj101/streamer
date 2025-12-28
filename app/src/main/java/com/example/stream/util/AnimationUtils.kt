package com.example.stream.util

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import kotlinx.coroutines.delay

/**
 * Standard animation durations
 */
object AnimationDurations {
    const val Fast = 150
    const val Normal = 300
    const val Slow = 500
}

/**
 * Standard spring configurations
 */
object SpringConfigs {
    val Default = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val Smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

/**
 * Fade in animation with delay
 */
@Composable
fun AnimatedFadeIn(
    visible: Boolean = true,
    delayMillis: Int = 0,
    durationMillis: Int = AnimationDurations.Normal,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible && delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = visible
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutLinearInEasing
            )
        )
    ) {
        content()
    }
}

/**
 * Slide in from bottom with fade
 */
@Composable
fun AnimatedSlideInVertically(
    visible: Boolean = true,
    delayMillis: Int = 0,
    durationMillis: Int = AnimationDurations.Normal,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible && delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = visible
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 4 }
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis, easing = FastOutLinearInEasing)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis, easing = FastOutLinearInEasing),
            targetOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

/**
 * Scale in animation with fade
 */
@Composable
fun AnimatedScaleIn(
    visible: Boolean = true,
    delayMillis: Int = 0,
    durationMillis: Int = AnimationDurations.Normal,
    initialScale: Float = 0.8f,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible && delayMillis > 0) {
            delay(delayMillis.toLong())
        }
        isVisible = visible
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        ) + scaleIn(
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
            initialScale = initialScale,
            transformOrigin = TransformOrigin.Center
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis, easing = FastOutLinearInEasing)
        ) + scaleOut(
            animationSpec = tween(durationMillis, easing = FastOutLinearInEasing),
            targetScale = initialScale,
            transformOrigin = TransformOrigin.Center
        )
    ) {
        content()
    }
}

/**
 * Crossfade between two states
 */
@Composable
fun <T> AnimatedCrossfade(
    targetState: T,
    durationMillis: Int = AnimationDurations.Normal,
    content: @Composable (T) -> Unit
) {
    androidx.compose.animation.Crossfade(
        targetState = targetState,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        )
    ) { state ->
        content(state)
    }
}

/**
 * Animated float value with spring
 */
@Composable
fun animateFloatAsState(
    targetValue: Float,
    spring: SpringSpec<Float> = SpringConfigs.Smooth
): State<Float> {
    return androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetValue,
        animationSpec = spring,
        label = "animatedFloat"
    )
}

/**
 * Staggered list item animation
 */
@Composable
fun StaggeredListItem(
    index: Int,
    delayPerItem: Long = 50L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(index * delayPerItem)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(AnimationDurations.Normal, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            animationSpec = tween(AnimationDurations.Normal, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 8 }
        )
    ) {
        content()
    }
}

/**
 * Pulsing animation for loading states
 */
@Composable
fun rememberPulseAnimation(
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 1f,
    durationMillis: Int = 1000
): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
}
