package com.example.gamebacklogtracker.domain.mapper

import com.example.gamebacklogtracker.data.remote.dto.GameDto
import com.example.gamebacklogtracker.domain.model.Game
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun GameDto.toDomainGame(): Game {
    val releaseInfo = releaseDates.toReleaseInfo()

    return Game(
        id = id ?: -1,
        name = name,
        imageUrl = image,
        description = description,
        sourceUrl = url,
        genres = genre.toStringList(),
        developers = developers.toStringList(),
        publishers = publishers.toStringList(),
        releaseJapan = releaseInfo.japan,
        releaseNorthAmerica = releaseInfo.northAmerica,
        releaseEurope = releaseInfo.europe,
        releaseAustralia = releaseInfo.australia,
    )
}

private fun JsonElement?.toStringList(): List<String> {
    if (this == null || this.isJsonNull) return emptyList()

    return when {
        isJsonPrimitive -> {
            asString.split(",", "/", ";")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }

        isJsonArray -> {
            asJsonArray.mapNotNull { element ->
                runCatching { element.asString.trim() }.getOrNull()
            }.filter { it.isNotBlank() }
        }

        else -> emptyList()
    }
}

private data class ReleaseInfo(
    val japan: String? = null,
    val northAmerica: String? = null,
    val europe: String? = null,
    val australia: String? = null,
)

private fun JsonElement?.toReleaseInfo(): ReleaseInfo {
    if (this == null || this.isJsonNull) return ReleaseInfo()

    return when {
        isJsonObject -> asJsonObject.toReleaseInfo()
        isJsonPrimitive -> {
            val value = asString.trim()
            ReleaseInfo(
                northAmerica = value.ifBlank { null }
            )
        }
        else -> ReleaseInfo()
    }
}

private fun JsonObject.toReleaseInfo(): ReleaseInfo {
    fun read(key: String): String? {
        val element = get(key) ?: return null
        if (element.isJsonNull) return null
        return runCatching { element.asString.trim() }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
    }

    return ReleaseInfo(
        japan = read("Japan"),
        northAmerica = read("NorthAmerica"),
        europe = read("Europe"),
        australia = read("Australia"),
    )
}