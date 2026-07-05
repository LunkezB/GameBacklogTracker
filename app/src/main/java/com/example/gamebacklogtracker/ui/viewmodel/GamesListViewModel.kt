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
import retrofit2.HttpException
import java.io.IOException

class GamesListViewModel(
    private val repository: GameRepository,
    private val status: GameStatus,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesListUiState())
    val uiState: StateFlow<GamesListUiState> = _uiState.asStateFlow()

    init {
        observeGames()
        refresh(initialLoad = true)
    }

    private fun observeGames() {
        viewModelScope.launch {
            repository.observeMyGamesByStatus(status).collect { games ->
                _uiState.update { currentState ->
                    currentState.copy(games = games)
                }
            }
        }
    }

    fun refresh() {
        refresh(initialLoad = false)
    }

    fun dismissError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    private fun refresh(initialLoad: Boolean) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = initialLoad,
                    isRefreshing = !initialLoad,
                    errorMessage = null,
                )
            }

            try {
                repository.refreshGames()
            } catch (_: IOException) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Couldn't update games. Check your internet connection.",
                    )
                }
                return@launch
            } catch (_: HttpException) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Server error while loading games. Please retry.",
                    )
                }
                return@launch
            } catch (e: Exception) {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = "Unexpected error: ${e.message}",
                )
            }
            e.printStackTrace()
            return@launch
        }

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                )
            }
        }
    }

    companion object {
        fun factory(status: GameStatus): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(GamesListViewModel::class.java)) {
                        return GamesListViewModel(
                            repository = AppGraph.gameRepository,
                            status = status,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}