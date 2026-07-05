package com.example.gamebacklogtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogtracker.data.repository.AppGraph
import com.example.gamebacklogtracker.data.repository.GameRepository
import com.example.gamebacklogtracker.domain.model.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameDetailsViewModel(
    private val repository: GameRepository,
    private val gameId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameDetailsUiState())
    val uiState: StateFlow<GameDetailsUiState> = _uiState.asStateFlow()

    init {
        observeGame()
    }

    private fun observeGame() {
        viewModelScope.launch {
            repository.observeGameDetails(gameId).collect { gameWithUserData ->
                _uiState.update { currentState ->
                    currentState.copy(
                        game = gameWithUserData,
                        selectedStatus = gameWithUserData?.userData?.status ?: GameStatus.NONE,
                        note = gameWithUserData?.userData?.note.orEmpty(),
                        hoursPlayedInput = gameWithUserData?.userData?.hoursPlayed?.toString().orEmpty(),
                        isLoading = false,
                        errorMessage = if (gameWithUserData == null) {
                            "Game not found."
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    fun onStatusSelected(status: GameStatus) {
        _uiState.update { it.copy(selectedStatus = status, saveSuccess = false) }
    }

    fun onNoteChanged(value: String) {
        _uiState.update { it.copy(note = value, saveSuccess = false) }
    }

    fun onHoursPlayedChanged(value: String) {
        val sanitized = value.filter { it.isDigit() }
        _uiState.update { it.copy(hoursPlayedInput = sanitized, saveSuccess = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun save() {
        val currentState = _uiState.value
        val currentGame = currentState.game ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

            try {
                repository.saveUserGameData(
                    gameId = currentGame.game.id,
                    status = currentState.selectedStatus,
                    note = currentState.note,
                    hoursPlayed = currentState.hoursPlayedInput.toIntOrNull(),
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Couldn't save changes.",
                    )
                }
            }
        }
    }

    companion object {
        fun factory(gameId: Int): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(GameDetailsViewModel::class.java)) {
                        return GameDetailsViewModel(
                            repository = AppGraph.gameRepository,
                            gameId = gameId,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}