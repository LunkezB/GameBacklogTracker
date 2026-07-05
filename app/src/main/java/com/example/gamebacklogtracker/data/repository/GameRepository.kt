package com.example.gamebacklogtracker.data.repository

import com.example.gamebacklogtracker.domain.model.Game
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import kotlinx.coroutines.flow.Flow

interface GameRepository {

    fun observeCatalogGames(): Flow<List<Game>>

    fun observeCatalogGamesWithUserData(): Flow<List<GameWithUserData>>

    fun observeGameDetails(gameId: Int): Flow<GameWithUserData?>

    fun observeMyGamesByStatus(status: GameStatus): Flow<List<GameWithUserData>>

    suspend fun refreshGames()

    suspend fun saveUserGameData(
        gameId: Int,
        status: GameStatus,
        note: String,
        hoursPlayed: Int?,
    )

    suspend fun deleteUserGameData(gameId: Int)
}