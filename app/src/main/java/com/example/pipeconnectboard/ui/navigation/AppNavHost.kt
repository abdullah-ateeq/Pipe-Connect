package com.example.pipeconnectboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pipeconnectboard.ui.screens.AboutScreen
import com.example.pipeconnectboard.ui.screens.GameScreen
import com.example.pipeconnectboard.ui.screens.HomeScreen
import com.example.pipeconnectboard.ui.screens.HowToPlayScreen
import com.example.pipeconnectboard.ui.screens.ResultScreen
import com.example.pipeconnectboard.ui.screens.SettingsScreen
import com.example.pipeconnectboard.ui.screens.SplashScreen
import com.example.pipeconnectboard.viewmodel.GameViewModel

@Composable
fun AppNavHost(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val gameState by viewModel.gameState.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                statistics = statistics,
                onPlayClicked = {
                    viewModel.startNewGame()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClicked = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClicked = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClicked = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                state = gameState,
                settings = userSettings,
                canUndo = canUndo,
                onCellClicked = { pos -> viewModel.placePipe(pos) },
                onTileSelected = { id -> viewModel.selectTile(id) },
                onRotate = { viewModel.rotateSelectedTile() },
                onUndo = { viewModel.undo() },
                onRestart = { viewModel.restart() },
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                state = gameState,
                onPlayAgainClicked = {
                    viewModel.startNewGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHomeClicked = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = userSettings,
                onThemeChanged = { viewModel.setThemeMode(it) },
                onBoardThemeChanged = { viewModel.setBoardTheme(it) },
                onSoundChanged = { viewModel.setSoundEnabled(it) },
                onHapticsChanged = { viewModel.setHapticEnabled(it) },
                onHighlightChanged = { viewModel.setHighlightConnected(it) },
                onTimerChanged = { viewModel.setShowTimer(it) },
                onResetStats = { viewModel.resetStatistics() },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}
