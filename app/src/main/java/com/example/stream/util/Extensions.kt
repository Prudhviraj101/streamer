package com.example.stream.util

import com.example.stream.util.Constants.IMAGE_SIZE_ORIGINAL
import com.example.stream.util.Constants.IMAGE_SIZE_W185
import com.example.stream.util.Constants.IMAGE_SIZE_W342
import com.example.stream.util.Constants.IMAGE_SIZE_W500
import com.example.stream.util.Constants.IMAGE_SIZE_W780
import com.example.stream.util.Constants.IMAGE_SIZE_W1280
import com.example.stream.util.Constants.TMDB_IMAGE_BASE_URL

fun String?.toImageUrl(size: String = IMAGE_SIZE_W500): String {
    return if (this != null) {
        "$TMDB_IMAGE_BASE_URL$size$this"
    } else {
        ""
    }
}

fun String?.toPosterUrl(): String = this.toImageUrl(IMAGE_SIZE_W342)
fun String?.toBackdropUrl(): String = this.toImageUrl(IMAGE_SIZE_W780)
fun String?.toBackdropUrlHQ(): String = this.toImageUrl(IMAGE_SIZE_W1280) // High quality for carousel
fun String?.toProfileUrl(): String {
    return if (this != null) {
        "${TMDB_IMAGE_BASE_URL}h632$this"
    } else {
        ""
    }
}

fun String?.toProfileUrlHQ(): String {
    return if (this != null) {
        "${TMDB_IMAGE_BASE_URL}original$this" // Original quality for cast profiles
    } else {
        ""
    }
}

fun Double?.toRatingString(): String {
    return this?.let { String.format("%.1f", it) } ?: "N/A"
}

fun Int?.toRuntimeString(): String {
    if (this == null || this == 0) return "N/A"
    val hours = this / 60
    val minutes = this % 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}
