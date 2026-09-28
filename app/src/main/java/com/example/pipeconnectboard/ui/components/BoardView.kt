package com.example.pipeconnectboard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.ui.theme.LocalBoardTheme

@Composable
fun BoardView(
    board: List<List<PipeCell>>,
    connectedCells: Set<GridPosition>,
    highlightConnected: Boolean,
    isComplete: Boolean,
    onCellClicked: (GridPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardTheme = LocalBoardTheme.current

    val infiniteTransition = rememberInfiniteTransition(label = "FlowPulse")
    val flowPulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlowPulsePhase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(boardTheme.boardBackground)
            .border(2.dp, boardTheme.gridBorder, RoundedCornerShape(14.dp))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (r in board.indices) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (c in board[r].indices) {
                        val position = GridPosition(r, c)
                        val cell = board[r][c]
                        val isConnected = highlightConnected && connectedCells.contains(position)

                        val cellDescription = when (cell) {
                            is PipeCell.Empty -> "Empty board cell, row ${r + 1}, column ${c + 1}"
                            is PipeCell.Blocked -> "Blocked steel panel, row ${r + 1}, column ${c + 1}"
                            is PipeCell.Source -> "Source inlet connecting right, row ${r + 1}, column ${c + 1}"
                            is PipeCell.Destination -> "Destination outlet accepting left, row ${r + 1}, column ${c + 1}" +
                                    if (isConnected) ", connected to pipeline" else ", disconnected"
                            is PipeCell.Pipe -> {
                                val baseDesc = cell.tile.accessibilityDescription()
                                val connDesc = if (isConnected) "connected to source" else "disconnected"
                                "$baseDesc, $connDesc, row ${r + 1}, column ${c + 1}"
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .border(0.5.dp, boardTheme.gridBorder.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .semantics { contentDescription = cellDescription }
                                .then(
                                    if (cell is PipeCell.Empty) {
                                        Modifier.clickable { onCellClicked(position) }
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            CellCanvas(
                                cell = cell,
                                isConnected = isConnected,
                                boardTheme = boardTheme,
                                flowPulsePhase = flowPulsePhase
                            )
                        }
                    }
                }
            }
        }
    }
}
