package com.example.pipeconnectboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeKind
import com.example.pipeconnectboard.model.PipeTile
import com.example.pipeconnectboard.ui.components.CellCanvas
import com.example.pipeconnectboard.ui.theme.AccentEmeraldDestination
import com.example.pipeconnectboard.ui.theme.AccentTealBright
import com.example.pipeconnectboard.ui.theme.LocalBoardTheme

@Composable
fun HowToPlayScreen(
    onBackClicked: () -> Unit
) {
    val boardTheme = LocalBoardTheme.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "HOW TO PLAY",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Example Diagram Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(boardTheme.boardBackground)
                .border(1.dp, boardTheme.gridBorder, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CONNECTION PRINCIPLE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = AccentTealBright
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Valid Matched Connection
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(52.dp)) {
                                CellCanvas(
                                    cell = PipeCell.Pipe(PipeTile(201, PipeKind.STRAIGHT, 0)),
                                    isConnected = true,
                                    boardTheme = boardTheme
                                )
                            }
                            Box(modifier = Modifier.size(52.dp)) {
                                CellCanvas(
                                    cell = PipeCell.Pipe(PipeTile(202, PipeKind.STRAIGHT, 0)),
                                    isConnected = true,
                                    boardTheme = boardTheme
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentEmeraldDestination,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Matched (Flows)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentEmeraldDestination
                            )
                        }
                    }

                    // Invalid Mismatched Connection
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(52.dp)) {
                                CellCanvas(
                                    cell = PipeCell.Pipe(PipeTile(203, PipeKind.STRAIGHT, 0)),
                                    isConnected = true,
                                    boardTheme = boardTheme
                                )
                            }
                            Box(modifier = Modifier.size(52.dp)) {
                                CellCanvas(
                                    cell = PipeCell.Pipe(PipeTile(204, PipeKind.STRAIGHT, 90)),
                                    isConnected = false,
                                    boardTheme = boardTheme
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Mismatched (Blocked)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rule Steps
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RuleCard(
                step = "01",
                title = "Select & Rotate",
                description = "Choose one of 3 available pipe pieces from your tray. Tap ROTATE to turn the selected piece 90° clockwise until properly oriented."
            )

            RuleCard(
                step = "02",
                title = "Place on Empty Cells",
                description = "Tap an empty board cell to position the pipe. Placed pipes cannot be moved, but you can always use UNDO to reverse your latest step."
            )

            RuleCard(
                step = "03",
                title = "Directional Matching",
                description = "Neighboring pipe openings must face each other to connect (e.g. RIGHT opening connects to a LEFT opening). Fluid flows automatically once aligned."
            )

            RuleCard(
                step = "04",
                title = "Source to Destination",
                description = "Establish an uninterrupted pipeline from the Source inlet on the left edge (row 2, col 0) to the Destination outlet on the right edge (row 3, col 5)."
            )

            RuleCard(
                step = "05",
                title = "Blocked Panels & Geometry",
                description = "Dark cross-hatched steel plates are non-interactive structural barriers. Pipes cannot be placed on blocked cells, source, or destination."
            )

            RuleCard(
                step = "06",
                title = "Branches & Loops Allowed",
                description = "You are free to form side branches and closed loops without penalty. Only a single valid route from source to destination is required to win."
            )

            RuleCard(
                step = "07",
                title = "Manage Your Tile Supply",
                description = "You have an inventory of 18 handcrafted tiles. Connect the pipeline before all tray and bag tiles are depleted."
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun RuleCard(step: String, title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
