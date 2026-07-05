package com.example.gamebacklogtracker.domain.mapper

import com.example.gamebacklogtracker.data.local.entity.GameEntity
import com.example.gamebacklogtracker.domain.model.Game

private const val LIST_SEPARATOR = " | "

fun GameEntity.toDomainGame(): Game {
    return Game(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        sourceUrl = sourceUrl,
        genres = genres.toListField(),
        developers = developers.toListField(),
        publishers = publishers.toListField(),
        releaseJapan = releaseJapan,
        releaseNorthAmerica = releaseNorthAmerica,
        releaseEurope = releaseEurope,
        releaseAustralia = releaseAustralia,
    )
}

fun Game.toEntity(): GameEntity {
    return GameEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        description = description,
        sourceUrl = sourceUrl,
        genres = genres.toDbField(),
        developers = developers.toDbField(),
        publishers = publishers.toDbField(),
        releaseJapan = releaseJapan,
        releaseNorthAmerica = releaseNorthAmerica,
        releaseEurope = releaseEurope,
        releaseAustralia = releaseAustralia,
    )
}

private fun List<String>.toDbField(): String {
    return joinToString(LIST_SEPARATOR)
}

private fun String.toListField(): List<String> {
    if (isBlank()) return emptyList()
    return split(LIST_SEPARATOR)
        .map { it.trim() }
        .filter { it.isNotBlank() }
}