package com.example.gamebacklogtracker.ui.viewmodel

import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData

data class GameDetailsUiState(
    val game: GameWithUserData? = null,
    val selectedStatus: GameStatus = GameStatus.NONE,
    val note: String = "",
    val hoursPlayedInput: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
)