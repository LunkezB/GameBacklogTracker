package com.example.gamebacklogtracker.data.remote

import com.example.gamebacklogtracker.data.remote.dto.GameDto
import retrofit2.http.GET

interface GameApiService {

    @GET("switch/games")
    suspend fun getGames(): List<GameDto>
}