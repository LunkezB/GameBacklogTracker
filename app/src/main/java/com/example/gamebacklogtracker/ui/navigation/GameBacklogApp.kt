package com.example.gamebacklogtracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamebacklogtracker.domain.model.GameStatus
import com.example.gamebacklogtracker.ui.screens.CatalogRoute
import com.example.gamebacklogtracker.ui.screens.GameDetailsRoute
import com.example.gamebacklogtracker.ui.screens.GamesListRoute

private sealed class BottomDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Backlog : BottomDestination(
        route = "backlog",
        title = "Backlog",
        selectedIcon = Icons.Rounded.Schedule,
        unselectedIcon = Icons.Outlined.Schedule,
    )

    data object Playing : BottomDestination(
        route = "playing",
        title = "Playing",
        selectedIcon = Icons.Rounded.PlayArrow,
        unselectedIcon = Icons.Outlined.PlayArrow,
    )

    data object Completed : BottomDestination(
        route = "completed",
        title = "Completed",
        selectedIcon = Icons.Rounded.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircleOutline,
    )
}

private object AppDestination {
    const val CATALOG_ROUTE = "catalog"
    const val DETAILS_ROUTE = "details/{gameId}"

    fun detailsRoute(gameId: Int): String = "details/$gameId"
}

private val bottomDestinations = listOf(
    BottomDestination.Backlog,
    BottomDestination.Playing,
    BottomDestination.Completed,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBacklogApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route

    val currentTitle = when (currentRoute) {
        BottomDestination.Backlog.route -> "Backlog"
        BottomDestination.Playing.route -> "Playing"
        BottomDestination.Completed.route -> "Completed"
        AppDestination.CATALOG_ROUTE -> "Browse Games"
        AppDestination.DETAILS_ROUTE -> "Game Details"
        else -> "Game Backlog Tracker"
    }

    val isTopLevelDestination = currentRoute in bottomDestinations.map { it.route }
    val showBottomBar = isTopLevelDestination
    val showFab = isTopLevelDestination

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = currentTitle) },
                navigationIcon = {
                    if (!isTopLevelDestination) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = destination.title,
                                )
                            },
                            label = {
                                Text(text = destination.title)
                            },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestination.CATALOG_ROUTE) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Browse games"
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Backlog.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding),
        ) {
            composable(BottomDestination.Backlog.route) {
                GamesListRoute(
                    status = GameStatus.BACKLOG,
                    emptyMessage = "No games in Backlog yet",
                    onGameClick = { gameId ->
                        navController.navigate(AppDestination.detailsRoute(gameId))
                    },
                )
            }

            composable(BottomDestination.Playing.route) {
                GamesListRoute(
                    status = GameStatus.PLAYING,
                    emptyMessage = "You are not playing anything yet",
                    onGameClick = { gameId ->
                        navController.navigate(AppDestination.detailsRoute(gameId))
                    },
                )
            }

            composable(BottomDestination.Completed.route) {
                GamesListRoute(
                    status = GameStatus.COMPLETED,
                    emptyMessage = "No completed games yet",
                    onGameClick = { gameId ->
                        navController.navigate(AppDestination.detailsRoute(gameId))
                    },
                )
            }

            composable(AppDestination.CATALOG_ROUTE) {
                CatalogRoute(
                    snackbarHostState = snackbarHostState,
                    onGameClick = { gameId ->
                        navController.navigate(AppDestination.detailsRoute(gameId))
                    },
                )
            }

            composable(
                route = AppDestination.DETAILS_ROUTE,
                arguments = listOf(
                    navArgument("gameId") {
                        type = NavType.IntType
                    }
                ),
            ) { entry ->
                val gameId = entry.arguments?.getInt("gameId") ?: -1

                GameDetailsRoute(
                    gameId = gameId,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}