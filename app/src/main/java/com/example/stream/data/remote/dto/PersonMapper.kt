package com.example.stream.data.remote.dto

import com.example.stream.domain.model.KnownWork
import com.example.stream.domain.model.PersonDetails

fun PersonDetailsDto.toPersonDetails(knownWorks: List<KnownWork>): PersonDetails {
    return PersonDetails(
        id = id,
        name = name,
        biography = biography ?: "",
        birthday = birthday,
        deathday = deathday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment ?: "Acting",
        knownFor = knownWorks
    )
}

fun CreditWorkDto.toKnownWork(): KnownWork {
    return KnownWork(
        id = id,
        title = title ?: name ?: "Unknown",
        posterPath = posterPath,
        mediaType = mediaType ?: "movie",
        voteAverage = voteAverage ?: 0.0
    )
}
