package com.example.gamebacklogtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.domain.model.GameWithUserData
import com.example.gamebacklogtracker.domain.model.gameStatusOptions
import com.example.gamebacklogtracker.ui.viewmodel.GameDetailsUiState
import com.example.gamebacklogtracker.ui.viewmodel.GameDetailsViewModel
import com.example.gamebacklogtracker.util.IntentUtils

@Composable
fun GameDetailsRoute(
    gameId: Int,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val viewModel: GameDetailsViewModel = viewModel(
        factory = GameDetailsViewModel.factory(gameId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Game updated")
            viewModel.consumeSaveSuccess()
        }
    }

    GameDetailsScreen(
        uiState = uiState,
        onStatusSelected = viewModel::onStatusSelected,
        onNoteChanged = viewModel::onNoteChanged,
        onHoursPlayedChanged = viewModel::onHoursPlayedChanged,
        onSaveClick = viewModel::save,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameDetailsScreen(
    uiState: GameDetailsUiState,
    onStatusSelected: (GameStatus) -> Unit,
    onNoteChanged: (String) -> Unit,
    onHoursPlayedChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.game == null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.errorMessage ?: "Game not found.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        else -> {
            val item = uiState.game

            androidx.compose.foundation.layout.Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (uiState.errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(text = uiState.errorMessage)
                            Button(onClick = onDismissError) {
                                Text("Dismiss")
                            }
                        }
                    }
                }

                GameHeader(
                    imageUrl = item.game.imageUrl,
                    title = item.displayTitle,
                    description = item.displayDescription,
                    metaLine = item.displayMetaLine,
                    releaseLine = item.displayReleaseLine,
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AssistChip(
                        onClick = { IntentUtils.shareGame(context, item) },
                        label = { Text("Share") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                            )
                        }
                    )

                    AssistChip(
                        onClick = { IntentUtils.openGameLinkOrSearch(context, item) },
                        label = { Text("Open link") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.OpenInBrowser,
                                contentDescription = null,
                            )
                        }
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium,
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gameStatusOptions.forEach { option ->
                        AssistChip(
                            onClick = { onStatusSelected(option.status) },
                            label = { Text(option.label) },
                            leadingIcon = {
                                if (uiState.selectedStatus == option.status) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = onNoteChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") },
                    placeholder = { Text("Add your thoughts about this game") },
                    minLines = 3,
                    maxLines = 5,
                )

                OutlinedTextField(
                    value = uiState.hoursPlayedInput,
                    onValueChange = onHoursPlayedChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Hours played") },
                    placeholder = { Text("e.g. 12") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                Button(
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 14.dp),
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Save")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GameHeader(
    imageUrl: String?,
    title: String,
    description: String,
    metaLine: String,
    releaseLine: String,
) {
    androidx.compose.foundation.layout.Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(MaterialTheme.shapes.large),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.SportsEsports,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )

        Text(
            text = metaLine,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = releaseLine,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}