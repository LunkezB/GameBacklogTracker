package com.example.gamebacklogtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.gamebacklogtracker.data.local.dao.GameDao
import com.example.gamebacklogtracker.data.local.dao.UserGameDao
import com.example.gamebacklogtracker.data.local.entity.GameEntity
import com.example.gamebacklogtracker.data.local.entity.UserGameEntity
import com.example.gamebacklogtracker.domain.model.GameStatus

@Database(
    entities = [
        GameEntity::class,
        UserGameEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(GameStatusConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun userGameDao(): UserGameDao
}

class GameStatusConverters {

    @TypeConverter
    fun fromGameStatus(value: GameStatus): String {
        return value.name
    }

    @TypeConverter
    fun toGameStatus(value: String): GameStatus {
        return runCatching { GameStatus.valueOf(value) }
            .getOrDefault(GameStatus.NONE)
    }
}