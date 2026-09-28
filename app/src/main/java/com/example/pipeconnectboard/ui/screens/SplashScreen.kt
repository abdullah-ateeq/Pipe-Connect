package com.example.pipeconnectboard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeKind
import com.example.pipeconnectboard.model.PipeTile
import com.example.pipeconnectboard.ui.components.CellCanvas
import com.example.pipeconnectboard.ui.theme.AccentTealBright
import com.example.pipeconnectboard.ui.theme.LocalBoardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val boardTheme = LocalBoardTheme.current

    // Animation progress controllers
    val gridAlpha = remember { Animatable(0f) }
    val endpointsScale = remember { Animatable(0.5f) }
    val endpointsAlpha = remember { Animatable(0f) }
    val piece1Rotation = remember { Animatable(90f) }
    val piece2Rotation = remember { Animatable(180f) }
    val flowConnectedAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.92f) }

    LaunchedEffect(Unit) {
        // Step 1: Grid fades in (0-300ms)
        gridAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // Step 2: Source & Destination appear (300-600ms)
        launch { endpointsAlpha.animateTo(1f, tween(300)) }
        endpointsScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // Step 3: Pipe pieces rotate into place (600-1000ms)
        launch { piece1Rotation.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
        piece2Rotation.animateTo(0f, tween(400, easing = FastOutSlowInEasing))

        // Step 4: Final piece completes pipeline, flow pulses (1000-1300ms)
        flowConnectedAlpha.animateTo(1f, tween(300))

        // Step 5: Title appears (1200-1500ms)
        launch { titleAlpha.animateTo(1f, tween(350)) }
        titleScale.animateTo(1f, tween(350, easing = FastOutSlowInEasing))

        // Hold briefly then proceed to Home (~1.6s total)
        delay(250)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Mini 4-cell pipeline demo animation
            Box(
                modifier = Modifier
                    .alpha(gridAlpha.value)
                    .clip(RoundedCornerShape(12.dp))
                    .background(boardTheme.boardBackground)
                    .border(1.5.dp, boardTheme.gridBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Source
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(endpointsScale.value)
                            .alpha(endpointsAlpha.value)
                    ) {
                        CellCanvas(
                            cell = PipeCell.Source,
                            isConnected = true,
                            boardTheme = boardTheme,
                            flowPulsePhase = flowConnectedAlpha.value
                        )
                    }

                    // Pipe 1 (Straight, rotating to 0 deg)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .rotate(piece1Rotation.value)
                    ) {
                        CellCanvas(
                            cell = PipeCell.Pipe(PipeTile(101, PipeKind.STRAIGHT, 0)),
                            isConnected = flowConnectedAlpha.value > 0.5f,
                            boardTheme = boardTheme,
                            flowPulsePhase = flowConnectedAlpha.value
                        )
                    }

                    // Pipe 2 (Cross, rotating to 0 deg)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .rotate(piece2Rotation.value)
                    ) {
                        CellCanvas(
                            cell = PipeCell.Pipe(PipeTile(102, PipeKind.CROSS, 0)),
                            isConnected = flowConnectedAlpha.value > 0.5f,
                            boardTheme = boardTheme,
                            flowPulsePhase = flowConnectedAlpha.value
                        )
                    }

                    // Destination
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(endpointsScale.value)
                            .alpha(endpointsAlpha.value)
                    ) {
                        CellCanvas(
                            cell = PipeCell.Destination,
                            isConnected = flowConnectedAlpha.value > 0.5f,
                            boardTheme = boardTheme,
                            flowPulsePhase = flowConnectedAlpha.value
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .scale(titleScale.value)
            ) {
                Text(
                    text = "PIPE CONNECT BOARD",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "MODULAR FLOW ENGINEERING",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                    color = AccentTealBright
                )
            }
        }
    }
}
