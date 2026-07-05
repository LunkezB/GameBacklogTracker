package com.example.gamebacklogtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gamebacklogtracker.data.local.entity.UserGameEntity
import com.example.gamebacklogtracker.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGameDao {

    @Query("SELECT * FROM user_games ORDER BY updatedAt DESC")
    fun observeAllUserGames(): Flow<List<UserGameEntity>>

    @Query("SELECT * FROM user_games WHERE gameId = :gameId LIMIT 1")
    fun observeUserGameById(gameId: Int): Flow<UserGameEntity?>

    @Query("SELECT * FROM user_games WHERE status = :status ORDER BY updatedAt DESC")
    fun observeUserGamesByStatus(status: GameStatus): Flow<List<UserGameEntity>>

    @Upsert
    suspend fun upsertUserGame(userGame: UserGameEntity)

    @Query("DELETE FROM user_games WHERE gameId = :gameId")
    suspend fun deleteByGameId(gameId: Int)
}