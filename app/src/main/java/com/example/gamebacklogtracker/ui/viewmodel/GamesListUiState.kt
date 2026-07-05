package com.example.gamebacklogtracker.ui.viewmodel

import com.example.gamebacklogtracker.domain.model.GameWithUserData

data class GamesListUiState(
    val games: List<GameWithUserData> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)