package com.example.gamebacklogtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamebacklogtracker.data.repository.AppGraph
import com.example.gamebacklogtracker.data.repository.GameRepository
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class CatalogUiState(
    val games: List<GameWithUserData> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val addedMessage: String? = null,
)

class CatalogViewModel(
    private val repository: GameRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        observeCatalog()
        refresh(initialLoad = true)
    }

    private fun observeCatalog() {
        viewModelScope.launch {
            repository.observeCatalogGamesWithUserData().collect { games ->
                _uiState.update { currentState ->
                    currentState.copy(
                        games = games,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = value)
        }
    }

    fun refresh() {
        refresh(initialLoad = false)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeAddedMessage() {
        _uiState.update { it.copy(addedMessage = null) }
    }

    fun quickAddToBacklog(item: GameWithUserData) {
        viewModelScope.launch {
            if (item.resolvedStatus == GameStatus.BACKLOG) {
                _uiState.update {
                    it.copy(addedMessage = "${item.displayTitle} is already in Backlog")
                }
                return@launch
            }

            try {
                repository.saveUserGameData(
                    gameId = item.game.id,
                    status = GameStatus.BACKLOG,
                    note = item.userData?.note.orEmpty(),
                    hoursPlayed = item.userData?.hoursPlayed,
                )

                _uiState.update {
                    it.copy(addedMessage = "${item.displayTitle} added to Backlog")
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Couldn't add game to Backlog.")
                }
            }
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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Couldn't load catalog. Check your internet connection.",
                    )
                }
                return@launch
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Server error while loading catalog.",
                    )
                }
                return@launch
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Unexpected error: ${e.message}",
                    )
                }
                e.printStackTrace()
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                )
            }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
                        return CatalogViewModel(
                            repository = AppGraph.gameRepository,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}