package com.example.gamebacklogtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gamebacklogtracker.domain.model.GameStatus

@Entity(
    tableName = "user_games",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["gameId"]),
        Index(value = ["status"]),
    ],
)
data class UserGameEntity(
    @PrimaryKey
    val gameId: Int,
    val status: GameStatus,
    val note: String,
    val hoursPlayed: Int?,
    val updatedAt: Long,
)