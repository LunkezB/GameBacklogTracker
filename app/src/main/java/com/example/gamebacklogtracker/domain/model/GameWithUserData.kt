package com.example.gamebacklogtracker.domain.model

data class GameWithUserData(
    val game: Game,
    val userData: UserGameData? = null,
) {
    val resolvedStatus: GameStatus
        get() = userData?.status ?: GameStatus.NONE

    val displayTitle: String
        get() = game.name?.takeIf { it.isNotBlank() } ?: "Unknown game"

    val displayDescription: String
        get() = when {
            !game.description.isNullOrBlank() -> game.description
            game.genres.isNotEmpty() && game.publishers.isNotEmpty() ->
                "${game.genres.first()} • ${game.publishers.first()}"
            game.genres.isNotEmpty() ->
                game.genres.joinToString(limit = 2, truncated = "…")
            game.publishers.isNotEmpty() ->
                "Publisher: ${game.publishers.first()}"
            game.developers.isNotEmpty() ->
                "Developer: ${game.developers.first()}"
            else -> "No description available."
        }

    val displayMetaLine: String
        get() {
            val parts = buildList {
                if (game.genres.isNotEmpty()) {
                    add(game.genres.joinToString(limit = 2, truncated = "…"))
                }
                if (game.publishers.isNotEmpty()) {
                    add(game.publishers.first())
                } else if (game.developers.isNotEmpty()) {
                    add(game.developers.first())
                }
            }

            return if (parts.isEmpty()) {
                "Nintendo Switch"
            } else {
                parts.joinToString(" • ")
            }
        }

    val displayReleaseLine: String
        get() = when {
            !game.releaseNorthAmerica.isNullOrBlank() -> "NA: ${game.releaseNorthAmerica}"
            !game.releaseEurope.isNullOrBlank() -> "EU: ${game.releaseEurope}"
            !game.releaseJapan.isNullOrBlank() -> "JP: ${game.releaseJapan}"
            !game.releaseAustralia.isNullOrBlank() -> "AU: ${game.releaseAustralia}"
            else -> "Release date unavailable"
        }

    val displayHoursPlayed: String
        get() = userData?.hoursPlayed?.let { "$it h played" } ?: "No playtime yet"
}