package com.example.stream.data.remote.dto

import com.example.stream.domain.model.Episode
import com.example.stream.domain.model.SeasonDetails

fun SeasonDetailsDto.toSeasonDetails(): SeasonDetails {
    return SeasonDetails(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath,
        airDate = airDate,
        episodes = episodes.map { it.toEpisode() }
    )
}

fun EpisodeDto.toEpisode(): Episode {
    return Episode(
        id = id,
        episodeNumber = episodeNumber,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview ?: "",
        stillPath = stillPath,
        airDate = airDate,
        runtime = runtime,
        voteAverage = voteAverage ?: 0.0
    )
}
