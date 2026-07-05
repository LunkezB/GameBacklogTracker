package com.example.gamebacklogtracker.domain.model

data class UserGameData(
    val gameId: Int,
    val status: GameStatus,
    val note: String,
    val hoursPlayed: Int?,
    val updatedAt: Long,
)