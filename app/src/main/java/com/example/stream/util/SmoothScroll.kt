package com.example.stream.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlin.math.abs

/**
 * Creates a smooth fling behavior for scrollable content
 */
@Composable
fun rememberSmoothScrollFlingBehavior(): FlingBehavior {
    val density = LocalDensity.current
    return remember(density) {
        SmoothScrollFlingBehavior(density)
    }
}

private class SmoothScrollFlingBehavior(
    private val density: Density
) : FlingBehavior {
    
    private val springSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
    
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // Calculate the target scroll based on velocity
        val targetScroll = initialVelocity / 2.5f
        
        // Perform smooth animated scroll
        var remainingVelocity = initialVelocity
        var lastValue = 0f
        
        // Animate the scroll with spring physics
        val velocityDecay = 0.92f // Slower decay for smoother feel
        var currentVelocity = initialVelocity
        
        while (abs(currentVelocity) > 0.3f) { // Lower threshold for longer animation
            val delta = currentVelocity * 0.016f // Approximate frame time
            scrollBy(delta)
            currentVelocity *= velocityDecay
            lastValue += delta
        }
        
        return remainingVelocity
    }
}
