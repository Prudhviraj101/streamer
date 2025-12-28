package com.example.stream.data.mapper

import com.example.stream.data.remote.dto.*
import com.example.stream.domain.model.*

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

fun MovieDetailsDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtime = runtime,
        genres = genres?.map { it.toGenre() } ?: emptyList(),
        tagline = tagline ?: ""
    )
}

fun TvShowDto.toTvShow(): TvShow {
    return TvShow(
        id = id,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        firstAirDate = firstAirDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

fun TvShowDetailsDto.toTvShow(): TvShow {
    return TvShow(
        id = id,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        firstAirDate = firstAirDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        numberOfSeasons = numberOfSeasons,
        numberOfEpisodes = numberOfEpisodes,
        genres = genres?.map { it.toGenre() } ?: emptyList(),
        tagline = tagline ?: ""
    )
}

fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun CastDto.toCast(): Cast {
    return Cast(
        id = id,
        name = name,
        character = character ?: "",
        profilePath = profilePath ?: ""
    )
}

fun MovieDto.toMediaItem(): MediaItem {
    return MediaItem(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        mediaType = MediaType.MOVIE
    )
}

fun TvShowDto.toMediaItem(): MediaItem {
    return MediaItem(
        id = id,
        title = name,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        releaseDate = firstAirDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        mediaType = MediaType.TV_SHOW
    )
}

fun VideoDto.toVideo(): Video {
    return Video(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type,
        official = official
    )
}
