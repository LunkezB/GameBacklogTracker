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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.gamebacklogtracker.ui.viewmodel.CatalogUiState
import com.example.gamebacklogtracker.ui.viewmodel.CatalogViewModel

@Composable
fun CatalogRoute(
    snackbarHostState: SnackbarHostState,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.factory()
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.addedMessage) {
        val message = uiState.addedMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumeAddedMessage()
    }

    CatalogScreen(
        uiState = uiState,
        onRetry = viewModel::refresh,
        onDismissError = viewModel::dismissError,
        onGameClick = onGameClick,
        onQuickAdd = viewModel::quickAddToBacklog,
        onSearchQueryChange = viewModel::updateSearchQuery,
        modifier = modifier,
    )
}

@Composable
fun CatalogScreen(
    uiState: CatalogUiState,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    onGameClick: (Int) -> Unit,
    onQuickAdd: (GameWithUserData) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredGames = remember(uiState.games, uiState.searchQuery) {
        val query = uiState.searchQuery.trim().lowercase()

        if (query.isBlank()) {
            uiState.games
        } else {
            uiState.games.filter { item ->
                item.displayTitle.lowercase().contains(query) ||
                        item.displayMetaLine.lowercase().contains(query) ||
                        item.displayDescription.lowercase().contains(query) ||
                        item.displayReleaseLine.lowercase().contains(query)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading && uiState.games.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.games.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No games found in catalog",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Try refreshing to load games from the API.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onRetry) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Refresh")
                    }
                }
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
                        CatalogErrorBanner(
                            message = uiState.errorMessage,
                            onRetry = onRetry,
                            onDismiss = onDismissError,
                        )
                    }

                    CatalogSearchBar(
                        query = uiState.searchQuery,
                        resultCount = filteredGames.size,
                        onQueryChange = onSearchQueryChange,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )

                    if (filteredGames.isEmpty()) {
                        SearchEmptyState(
                            query = uiState.searchQuery,
                            onClear = { onSearchQueryChange("") },
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                items = filteredGames,
                                key = { item -> item.game.id }
                            ) { item ->
                                CatalogGameCard(
                                    item = item,
                                    onClick = { onGameClick(item.game.id) },
                                    onQuickAdd = { onQuickAdd(item) },
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
        }
    }
}

@Composable
private fun CatalogSearchBar(
    query: String,
    resultCount: Int,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Search games") },
            placeholder = { Text("Name, genre, publisher...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Clear search",
                        )
                    }
                }
            },
        )

        Text(
            text = "$resultCount result(s)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchEmptyState(
    query: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No matches for \"$query\"",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try another title, genre, publisher or developer.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onClear) {
            Text("Clear search")
        }
    }
}

@Composable
private fun CatalogErrorBanner(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(
        modifier = Modifier
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
private fun CatalogGameCard(
    item: GameWithUserData,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canQuickAdd = item.resolvedStatus != GameStatus.BACKLOG

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
            if (!item.game.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.game.imageUrl,
                    contentDescription = item.displayTitle,
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                AssistChip(
                    onClick = {},
                    label = { Text(statusLabel(item.resolvedStatus)) }
                )

                Text(
                    text = item.displayMetaLine,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = item.displayReleaseLine,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                HorizontalDivider(modifier = Modifier.padding(top = 6.dp))

                Text(
                    text = "Tap card for details, or use + to add to Backlog.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FilledTonalIconButton(
                onClick = onQuickAdd,
                enabled = canQuickAdd,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add to Backlog"
                )
            }
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