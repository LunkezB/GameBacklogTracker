package com.example.gamebacklogtracker.data.remote.dto

import com.google.gson.JsonElement

data class GameDto(
    val id: Int?,
    val name: String?,
    val image: String?,
    val description: String?,
    val url: String?,
    val genre: JsonElement?,
    val developers: JsonElement?,
    val publishers: JsonElement?,
    val releaseDates: JsonElement?,
)