package com.example.gamebacklogtracker.domain.mapper

import com.example.gamebacklogtracker.data.local.entity.UserGameEntity
import com.example.gamebacklogtracker.domain.model.UserGameData

fun UserGameEntity.toDomainUserGameData(): UserGameData {
    return UserGameData(
        gameId = gameId,
        status = status,
        note = note,
        hoursPlayed = hoursPlayed,
        updatedAt = updatedAt,
    )
}

fun UserGameData.toEntity(): UserGameEntity {
    return UserGameEntity(
        gameId = gameId,
        status = status,
        note = note,
        hoursPlayed = hoursPlayed,
        updatedAt = updatedAt,
    )
}