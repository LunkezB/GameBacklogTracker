package com.example.gamebacklogtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val imageUrl: String?,
    val description: String?,
    val sourceUrl: String?,
    val genres: String,
    val developers: String,
    val publishers: String,
    val releaseJapan: String?,
    val releaseNorthAmerica: String?,
    val releaseEurope: String?,
    val releaseAustralia: String?,
)