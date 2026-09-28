package com.example.pipeconnectboard.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeTile
import com.example.pipeconnectboard.ui.theme.AccentTealBright
import com.example.pipeconnectboard.ui.theme.LocalBoardTheme

@Composable
fun TrayView(
    tray: List<PipeTile>,
    selectedTileId: Int?,
    remainingInBag: Int,
    onTileSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardTheme = LocalBoardTheme.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AVAILABLE TILES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "+$remainingInBag in bag",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Display exactly 3 slots
            for (slotIndex in 0 until 3) {
                val tile = tray.getOrNull(slotIndex)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (tile != null) {
                        val isSelected = tile.id == selectedTileId

                        val offsetY by animateDpAsState(
                            targetValue = if (isSelected) (-6).dp else 0.dp,
                            animationSpec = tween(150),
                            label = "LiftOffset"
                        )
                        val elevation by animateDpAsState(
                            targetValue = if (isSelected) 8.dp else 2.dp,
                            animationSpec = tween(150),
                            label = "Elevation"
                        )
                        val rotationAngle by animateFloatAsState(
                            targetValue = tile.rotation.toFloat(),
                            animationSpec = tween(140),
                            label = "TileRotation"
                        )

                        val borderColor = if (isSelected) {
                            AccentTealBright
                        } else {
                            boardTheme.gridBorder
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = offsetY)
                                .shadow(elevation, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(boardTheme.boardBackground)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onTileSelected(tile.id) }
                                .semantics {
                                    contentDescription = "${tile.accessibilityDescription()}${if (isSelected) ", selected" else ""}"
                                }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Render pipe
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(rotationAngle)
                            ) {
                                CellCanvas(
                                    // Use base rotation tile for canvas, rotating the outer box
                                    cell = PipeCell.Pipe(tile.copy(rotation = 0)),
                                    isConnected = false,
                                    boardTheme = boardTheme
                                )
                            }

                            // Active indicator badge
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(8.dp)
                                        .background(AccentTealBright, CircleShape)
                                )
                            }

                            // Degree badge
                            Text(
                                text = "${tile.rotation}°",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(2.dp)
                            )
                        }
                    } else {
                        // Empty slot plate
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(boardTheme.emptyCellBackground.copy(alpha = 0.4f))
                                .border(1.dp, boardTheme.gridBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .semantics { contentDescription = "Empty tray slot" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "EMPTY",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}
