package com.example.pipeconnectboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.pipeconnectboard.data.UserSettings
import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeConnectState
import com.example.pipeconnectboard.ui.components.ActionControls
import com.example.pipeconnectboard.ui.components.BoardView
import com.example.pipeconnectboard.ui.components.GameHud
import com.example.pipeconnectboard.ui.components.IndustrialDialog
import com.example.pipeconnectboard.ui.components.TrayView
import com.example.pipeconnectboard.ui.theme.AccentEmeraldDestination
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    state: PipeConnectState,
    settings: UserSettings,
    canUndo: Boolean,
    onCellClicked: (GridPosition) -> Unit,
    onTileSelected: (Int) -> Unit,
    onRotate: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onBackToHome: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    var showRestartDialog by remember { mutableStateOf(false) }

    // Auto navigate to result screen after brief win/loss fanfare animation
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            delay(1200L)
            onNavigateToResult()
        }
    }

    LaunchedEffect(state.isFailed) {
        if (state.isFailed) {
            delay(1000L)
            onNavigateToResult()
        }
    }

    if (showRestartDialog) {
        IndustrialDialog(
            title = "Restart Puzzle",
            message = "Are you sure you want to restart this puzzle? Current progress will be lost.",
            confirmText = "Restart",
            cancelText = "Cancel",
            onConfirm = {
                showRestartDialog = false
                onRestart()
            },
            onDismiss = { showRestartDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // Landscape / Tablet Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Board
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardView(
                            board = state.board,
                            connectedCells = state.connectedCells,
                            highlightConnected = settings.highlightConnected,
                            isComplete = state.isComplete,
                            onCellClicked = onCellClicked
                        )
                    }

                    // Right Column: HUD, Tray, and Controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackToHome) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Home",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "PIPE CONNECT BOARD",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        GameHud(
                            isConnected = state.isConnected,
                            tilesLeft = state.pipesRemaining,
                            moves = state.moveCount,
                            elapsedMillis = state.elapsedMillis,
                            showTimer = settings.showTimer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TrayView(
                            tray = state.tray,
                            selectedTileId = state.selectedTileId,
                            remainingInBag = state.bag.size,
                            onTileSelected = onTileSelected
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ActionControls(
                            canRotate = state.selectedTile != null && !state.isComplete && !state.isFailed && !state.isAnimating,
                            canUndo = canUndo && !state.isComplete && !state.isFailed && !state.isAnimating,
                            onRotate = onRotate,
                            onUndo = onUndo,
                            onRestart = { showRestartDialog = true }
                        )
                    }
                }
            } else {
                // Portrait Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top App Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackToHome) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Home",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "PIPE CONNECT BOARD",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact HUD
                    GameHud(
                        isConnected = state.isConnected,
                        tilesLeft = state.pipesRemaining,
                        moves = state.moveCount,
                        elapsedMillis = state.elapsedMillis,
                        showTimer = settings.showTimer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6x6 Board
                    BoardView(
                        board = state.board,
                        connectedCells = state.connectedCells,
                        highlightConnected = settings.highlightConnected,
                        isComplete = state.isComplete,
                        onCellClicked = onCellClicked
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pipe Tray
                    TrayView(
                        tray = state.tray,
                        selectedTileId = state.selectedTileId,
                        remainingInBag = state.bag.size,
                        onTileSelected = onTileSelected
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Controls
                    ActionControls(
                        canRotate = state.selectedTile != null && !state.isComplete && !state.isFailed && !state.isAnimating,
                        canUndo = canUndo && !state.isComplete && !state.isFailed && !state.isAnimating,
                        onRotate = onRotate,
                        onUndo = onUndo,
                        onRestart = { showRestartDialog = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Win Banner Overlay
        AnimatedVisibility(
            visible = state.isComplete,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, AccentEmeraldDestination, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentEmeraldDestination,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "PIPELINE COMPLETE",
                        style = MaterialTheme.typography.titleMedium,
                        color = AccentEmeraldDestination
                    )
                }
            }
        }

        // Failure Banner Overlay
        AnimatedVisibility(
            visible = state.isFailed,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "CONNECTION INCOMPLETE",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
