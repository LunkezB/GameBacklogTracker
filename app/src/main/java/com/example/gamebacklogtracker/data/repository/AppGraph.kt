package com.example.gamebacklogtracker.data.repository

import android.content.Context
import com.example.gamebacklogtracker.data.local.LocalModule
import com.example.gamebacklogtracker.data.remote.NetworkModule

object AppGraph {

    @Volatile
    private var initialized = false

    lateinit var gameRepository: GameRepository
        private set

    fun init(context: Context) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            val database = LocalModule.provideDatabase(context)

            gameRepository = OfflineFirstGameRepository(
                apiService = NetworkModule.gameApiService,
                gameDao = database.gameDao(),
                userGameDao = database.userGameDao(),
            )

            initialized = true
        }
    }
}