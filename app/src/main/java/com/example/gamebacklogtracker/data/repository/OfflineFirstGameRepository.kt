package com.example.gamebacklogtracker.data.repository

import com.example.gamebacklogtracker.data.local.dao.GameDao
import com.example.gamebacklogtracker.data.local.dao.UserGameDao
import com.example.gamebacklogtracker.domain.mapper.toDomainGame
import com.example.gamebacklogtracker.domain.mapper.toDomainUserGameData
import com.example.gamebacklogtracker.domain.mapper.toEntity
import com.example.gamebacklogtracker.domain.mapper.toDomainGame as dtoToDomainGame
import com.example.gamebacklogtracker.domain.model.Game
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import com.example.gamebacklogtracker.data.local.entity.UserGameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class OfflineFirstGameRepository(
    private val apiService: com.example.gamebacklogtracker.data.remote.GameApiService,
    private val gameDao: GameDao,
    private val userGameDao: UserGameDao,
) : GameRepository {

    override fun observeCatalogGames(): Flow<List<Game>> {
        return gameDao.observeAllGames()
            .map { entities ->
                entities.map { it.toDomainGame() }
            }
    }

    override fun observeCatalogGamesWithUserData(): Flow<List<GameWithUserData>> {
        return combine(
            gameDao.observeAllGames(),
            userGameDao.observeAllUserGames(),
        ) { gameEntities, userEntities ->
            val userDataByGameId = userEntities.associateBy { it.gameId }

            gameEntities.map { gameEntity ->
                GameWithUserData(
                    game = gameEntity.toDomainGame(),
                    userData = userDataByGameId[gameEntity.id]?.toDomainUserGameData(),
                )
            }
        }
    }

    override fun observeGameDetails(gameId: Int): Flow<GameWithUserData?> {
        return combine(
            gameDao.observeGameById(gameId),
            userGameDao.observeUserGameById(gameId),
        ) { gameEntity, userGameEntity ->
            gameEntity?.let { entity ->
                GameWithUserData(
                    game = entity.toDomainGame(),
                    userData = userGameEntity?.toDomainUserGameData(),
                )
            }
        }
    }

    override fun observeMyGamesByStatus(status: GameStatus): Flow<List<GameWithUserData>> {
        return combine(
            gameDao.observeAllGames(),
            userGameDao.observeUserGamesByStatus(status),
        ) { gameEntities, userEntities ->
            val gamesById = gameEntities.associateBy { it.id }

            userEntities.mapNotNull { userEntity ->
                val gameEntity = gamesById[userEntity.gameId] ?: return@mapNotNull null
                GameWithUserData(
                    game = gameEntity.toDomainGame(),
                    userData = userEntity.toDomainUserGameData(),
                )
            }
        }
    }

    override suspend fun refreshGames() {
        val remoteGames = apiService.getGames()

        val validEntities = remoteGames.mapNotNull { dto ->
            val gameId = dto.id ?: return@mapNotNull null
            dto.dtoToDomainGame()
                .copy(id = gameId)
                .toEntity()
        }

        gameDao.upsertGames(validEntities)
    }

    override suspend fun saveUserGameData(
        gameId: Int,
        status: GameStatus,
        note: String,
        hoursPlayed: Int?,
    ) {
        if (status == GameStatus.NONE) {
            deleteUserGameData(gameId)
            return
        }

        val sanitizedNote = note.trim()
        val sanitizedHoursPlayed = hoursPlayed?.coerceAtLeast(0)

        val userGameEntity = UserGameEntity(
            gameId = gameId,
            status = status,
            note = sanitizedNote,
            hoursPlayed = sanitizedHoursPlayed,
            updatedAt = System.currentTimeMillis(),
        )

        userGameDao.upsertUserGame(userGameEntity)
    }

    override suspend fun deleteUserGameData(gameId: Int) {
        userGameDao.deleteByGameId(gameId)
    }
}