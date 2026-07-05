package com.example.gamebacklogtracker.domain.model

data class Game(
    val id: Int,
    val name: String?,
    val imageUrl: String?,
    val description: String?,
    val sourceUrl: String?,
    val genres: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val releaseJapan: String? = null,
    val releaseNorthAmerica: String? = null,
    val releaseEurope: String? = null,
    val releaseAustralia: String? = null,
)