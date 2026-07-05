package com.example.gamebacklogtracker.domain.model

data class GameStatusUi(
    val status: GameStatus,
    val label: String,
)

val gameStatusOptions = listOf(
    GameStatusUi(GameStatus.NONE, "None"),
    GameStatusUi(GameStatus.BACKLOG, "Backlog"),
    GameStatusUi(GameStatus.PLAYING, "Playing"),
    GameStatusUi(GameStatus.COMPLETED, "Completed"),
)