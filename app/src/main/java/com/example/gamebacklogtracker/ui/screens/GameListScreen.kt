package com.example.gamebacklogtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import com.example.gamebacklogtracker.ui.viewmodel.GamesListUiState
import com.example.gamebacklogtracker.ui.viewmodel.GamesListViewModel

@Composable
fun GamesListRoute(
    status: GameStatus,
    emptyMessage: String,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: GamesListViewModel = viewModel(
        factory = GamesListViewModel.factory(status)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GamesListScreen(
        uiState = uiState,
        emptyMessage = emptyMessage,
        onRetry = viewModel::refresh,
        onDismissError = viewModel::dismissError,
        onGameClick = onGameClick,
        modifier = modifier,
    )
}

@Composable
fun GamesListScreen(
    uiState: GamesListUiState,
    emptyMessage: String,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading && uiState.games.isEmpty() -> {
                LoadingState()
            }

            uiState.games.isEmpty() -> {
                EmptyState(
                    message = emptyMessage,
                    onRetry = onRetry,
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.isRefreshing) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.errorMessage != null) {
                        ErrorBanner(
                            message = uiState.errorMessage,
                            onRetry = onRetry,
                            onDismiss = onDismissError,
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.games,
                            key = { item -> item.game.id }
                        ) { item ->
                            GameCard(
                                item = item,
                                onClick = { onGameClick(item.game.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        if (!uiState.isLoading && uiState.games.isEmpty() && uiState.errorMessage != null) {
            ErrorBanner(
                message = uiState.errorMessage,
                onRetry = onRetry,
                onDismiss = onDismissError,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading your games...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = "Games will appear here after you assign a status.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(onClick = onRetry) {
                Text(text = "Refresh")
            }
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }

                TextButton(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    item: GameWithUserData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = item.displayTitle
    val description = item.displayDescription
    val metaLine = item.displayMetaLine
    val releaseLine = item.displayReleaseLine
    val hoursText = remember(item.userData?.hoursPlayed) {
        item.userData?.hoursPlayed?.let { "$it h played" } ?: "No playtime yet"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GameCover(
                imageUrl = item.game.imageUrl,
                title = title,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(text = statusLabel(item.resolvedStatus))
                    }
                )

                Text(
                    text = metaLine,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = releaseLine,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = hoursText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "Open details"
                )
            }
        }
    }
}

@Composable
private fun GameCover(
    imageUrl: String?,
    title: String,
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .height(96.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
        )
    } else {
        Box(
            modifier = Modifier
                .height(96.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsEsports,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun statusLabel(status: GameStatus): String {
    return when (status) {
        GameStatus.NONE -> "None"
        GameStatus.BACKLOG -> "Backlog"
        GameStatus.PLAYING -> "Playing"
        GameStatus.COMPLETED -> "Completed"
    }
}