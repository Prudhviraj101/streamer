package com.example.stream.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Window size classes for responsive design
 */
enum class WindowSizeClass {
    Compact,    // < 600dp width (phones in portrait)
    Medium,     // 600-840dp width (large phones, small tablets)
    Expanded    // > 840dp width (tablets, foldables)
}

/**
 * Get the current window size class based on screen width
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    
    return remember(screenWidthDp) {
        when {
            screenWidthDp < 600.dp -> WindowSizeClass.Compact
            screenWidthDp < 840.dp -> WindowSizeClass.Medium
            else -> WindowSizeClass.Expanded
        }
    }
}

/**
 * Get adaptive number of columns for grid layouts
 */
fun WindowSizeClass.getGridColumns(
    compact: Int = 2,
    medium: Int = 3,
    expanded: Int = 4
): Int = when (this) {
    WindowSizeClass.Compact -> compact
    WindowSizeClass.Medium -> medium
    WindowSizeClass.Expanded -> expanded
}

/**
 * Get adaptive horizontal padding
 */
fun WindowSizeClass.getHorizontalPadding(): Dp = when (this) {
    WindowSizeClass.Compact -> 16.dp
    WindowSizeClass.Medium -> 24.dp
    WindowSizeClass.Expanded -> 32.dp
}

/**
 * Get adaptive vertical spacing
 */
fun WindowSizeClass.getVerticalSpacing(): Dp = when (this) {
    WindowSizeClass.Compact -> 16.dp
    WindowSizeClass.Medium -> 20.dp
    WindowSizeClass.Expanded -> 24.dp
}

/**
 * Get adaptive item spacing for grids
 */
fun WindowSizeClass.getItemSpacing(): Dp = when (this) {
    WindowSizeClass.Compact -> 12.dp
    WindowSizeClass.Medium -> 16.dp
    WindowSizeClass.Expanded -> 20.dp
}

/**
 * Get adaptive title font size
 */
fun WindowSizeClass.getTitleFontSize(): TextUnit = when (this) {
    WindowSizeClass.Compact -> 32.sp
    WindowSizeClass.Medium -> 40.sp
    WindowSizeClass.Expanded -> 48.sp
}

/**
 * Get adaptive headline font size
 */
fun WindowSizeClass.getHeadlineFontSize(): TextUnit = when (this) {
    WindowSizeClass.Compact -> 24.sp
    WindowSizeClass.Medium -> 28.sp
    WindowSizeClass.Expanded -> 32.sp
}

/**
 * Get adaptive body font size
 */
fun WindowSizeClass.getBodyFontSize(): TextUnit = when (this) {
    WindowSizeClass.Compact -> 14.sp
    WindowSizeClass.Medium -> 16.sp
    WindowSizeClass.Expanded -> 18.sp
}

/**
 * Get adaptive button height
 */
fun WindowSizeClass.getButtonHeight(): Dp = when (this) {
    WindowSizeClass.Compact -> 48.dp
    WindowSizeClass.Medium -> 52.dp
    WindowSizeClass.Expanded -> 56.dp
}

/**
 * Get adaptive icon size
 */
fun WindowSizeClass.getIconSize(): Dp = when (this) {
    WindowSizeClass.Compact -> 24.dp
    WindowSizeClass.Medium -> 28.dp
    WindowSizeClass.Expanded -> 32.dp
}

/**
 * Check if screen is small (useful for conditional layouts)
 */
@Composable
fun isSmallScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp < 360
}

/**
 * Get screen width in dp
 */
@Composable
fun getScreenWidthDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

/**
 * Get screen height in dp
 */
@Composable
fun getScreenHeightDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp
}
