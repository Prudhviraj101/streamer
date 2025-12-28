package com.example.stream.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stream.ui.theme.*
import com.example.stream.util.toRatingString

@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val ratingColor = when {
        rating >= 8.0 -> RatingGreen
        rating >= 6.0 -> RatingYellow
        rating >= 4.0 -> RatingGold
        else -> RatingRed
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .glassMorphism(cornerRadius = 8.dp, borderWidth = 0.5.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = ratingColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = rating.toRatingString(),
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun GenreChip(
    genre: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .glassMorphism(cornerRadius = 16.dp, borderWidth = 0.5.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
