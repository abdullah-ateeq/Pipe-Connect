package com.example.pipeconnectboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.pipeconnectboard.model.Direction
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeTile
import com.example.pipeconnectboard.ui.theme.BoardThemeColors

@Composable
fun CellCanvas(
    cell: PipeCell,
    isConnected: Boolean,
    boardTheme: BoardThemeColors,
    flowPulsePhase: Float = 0f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        when (cell) {
            is PipeCell.Empty -> drawEmptyCell(boardTheme)
            is PipeCell.Blocked -> drawBlockedCell(boardTheme)
            is PipeCell.Source -> drawSourceCell(boardTheme, flowPulsePhase)
            is PipeCell.Destination -> drawDestinationCell(isConnected, boardTheme, flowPulsePhase)
            is PipeCell.Pipe -> drawPipeTileCell(
                tile = cell.tile,
                isConnected = isConnected,
                boardTheme = boardTheme,
                flowPulsePhase = flowPulsePhase
            )
        }
    }
}

private fun DrawScope.drawEmptyCell(theme: BoardThemeColors) {
    // Subtle background
    drawRect(color = theme.emptyCellBackground)

    // Engineering alignment crosshair at center
    val cx = size.width / 2f
    val cy = size.height / 2f
    val crossLen = size.width * 0.12f

    drawLine(
        color = theme.emptyCellCross,
        start = Offset(cx - crossLen, cy),
        end = Offset(cx + crossLen, cy),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = theme.emptyCellCross,
        start = Offset(cx, cy - crossLen),
        end = Offset(cx, cy + crossLen),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawBlockedCell(theme: BoardThemeColors) {
    // Solid plate
    drawRect(color = theme.blockedPlateBackground)

    // Diagonal hazard cross-hatch stripes
    val step = size.width * 0.22f
    val strokeW = size.width * 0.06f

    var offset = -size.width
    while (offset < size.width * 2) {
        drawLine(
            color = theme.blockedHatchColor,
            start = Offset(offset, 0f),
            end = Offset(offset + size.height, size.height),
            strokeWidth = strokeW,
            cap = StrokeCap.Square
        )
        offset += step
    }

    // Plate rivet rivets in 4 corners
    val r = size.width * 0.04f
    val margin = size.width * 0.12f
    val rivetColor = theme.gridBorder
    drawCircle(rivetColor, radius = r, center = Offset(margin, margin))
    drawCircle(rivetColor, radius = r, center = Offset(size.width - margin, margin))
    drawCircle(rivetColor, radius = r, center = Offset(margin, size.height - margin))
    drawCircle(rivetColor, radius = r, center = Offset(size.width - margin, size.height - margin))
}

private fun DrawScope.drawSourceCell(theme: BoardThemeColors, pulse: Float) {
    drawRect(color = theme.emptyCellBackground)

    val cx = size.width / 2f
    val cy = size.height / 2f
    val pipeThickness = size.width * 0.44f

    // Source inlet chamber on left
    val inletWidth = size.width * 0.35f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(theme.sourceColor.copy(alpha = 0.85f), theme.pipeBody),
            startX = 0f,
            endX = inletWidth
        ),
        topLeft = Offset(0f, cy - pipeThickness / 2f),
        size = Size(inletWidth, pipeThickness)
    )

    // Inlet flange plate on left border
    drawRect(
        color = theme.sourceColor,
        topLeft = Offset(0f, cy - pipeThickness * 0.65f),
        size = Size(size.width * 0.08f, pipeThickness * 1.3f)
    )

    // Pipe section going towards right
    drawRect(
        color = theme.pipeBody,
        topLeft = Offset(inletWidth, cy - pipeThickness / 2f),
        size = Size(size.width - inletWidth, pipeThickness)
    )

    // Inner flow channel
    val channelW = pipeThickness * 0.5f
    drawRect(
        color = theme.pipeCore,
        topLeft = Offset(size.width * 0.08f, cy - channelW / 2f),
        size = Size(size.width - size.width * 0.08f, channelW)
    )

    // Flow core line glowing amber/ivory toward right
    val flowAlpha = 0.8f + (pulse * 0.2f)
    drawLine(
        color = theme.sourceColor.copy(alpha = flowAlpha),
        start = Offset(0f, cy),
        end = Offset(size.width, cy),
        strokeWidth = channelW * 0.55f,
        cap = StrokeCap.Square
    )

    // Flow direction chevron pointing right
    val chevronPath = Path().apply {
        moveTo(cx - 4f, cy - 8f)
        lineTo(cx + 4f, cy)
        lineTo(cx - 4f, cy + 8f)
    }
    drawPath(
        path = chevronPath,
        color = Color.White.copy(alpha = 0.9f),
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )

    // Source indicator ring
    drawCircle(
        color = theme.sourceColor,
        radius = size.width * 0.2f,
        center = Offset(size.width * 0.22f, cy),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawDestinationCell(
    isConnected: Boolean,
    theme: BoardThemeColors,
    pulse: Float
) {
    drawRect(color = theme.emptyCellBackground)

    val cy = size.height / 2f
    val pipeThickness = size.width * 0.44f
    val outletX = size.width * 0.65f

    // Receiving pipe coming from left
    drawRect(
        color = theme.pipeBody,
        topLeft = Offset(0f, cy - pipeThickness / 2f),
        size = Size(outletX, pipeThickness)
    )

    // Receiving channel
    val channelW = pipeThickness * 0.5f
    drawRect(
        color = theme.pipeCore,
        topLeft = Offset(0f, cy - channelW / 2f),
        size = Size(outletX, channelW)
    )

    // Outlet chamber on right
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(theme.pipeBody, theme.destinationColor.copy(alpha = 0.85f)),
            startX = outletX,
            endX = size.width
        ),
        topLeft = Offset(outletX, cy - pipeThickness / 2f),
        size = Size(size.width - outletX, pipeThickness)
    )

    // Destination collar plate on right border
    drawRect(
        color = theme.destinationColor,
        topLeft = Offset(size.width - size.width * 0.08f, cy - pipeThickness * 0.65f),
        size = Size(size.width * 0.08f, pipeThickness * 1.3f)
    )

    // If connected, show bright active emerald flow!
    if (isConnected) {
        val flowAlpha = 0.8f + (pulse * 0.2f)
        drawLine(
            color = theme.destinationColor.copy(alpha = flowAlpha),
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = channelW * 0.6f,
            cap = StrokeCap.Square
        )
    }

    // Terminal socket ring
    drawCircle(
        color = theme.destinationColor,
        radius = size.width * 0.2f,
        center = Offset(size.width * 0.78f, cy),
        style = Stroke(width = 3.5f)
    )

    // Small receiver dot in socket
    val dotColor = if (isConnected) theme.destinationColor else theme.destinationColor.copy(alpha = 0.5f)
    drawCircle(
        color = dotColor,
        radius = size.width * 0.08f,
        center = Offset(size.width * 0.78f, cy)
    )
}

fun DrawScope.drawPipeTileCell(
    tile: PipeTile,
    isConnected: Boolean,
    boardTheme: BoardThemeColors,
    flowPulsePhase: Float = 0f
) {
    drawRect(color = boardTheme.emptyCellBackground)

    val cx = size.width / 2f
    val cy = size.height / 2f
    val pipeThickness = size.width * 0.44f
    val halfPipe = pipeThickness / 2f
    val channelThickness = pipeThickness * 0.48f
    val connections = tile.connections

    // 1. Draw outer metallic pipe segments
    for (dir in connections) {
        when (dir) {
            Direction.UP -> drawRect(
                color = boardTheme.pipeBody,
                topLeft = Offset(cx - halfPipe, 0f),
                size = Size(pipeThickness, cy)
            )
            Direction.DOWN -> drawRect(
                color = boardTheme.pipeBody,
                topLeft = Offset(cx - halfPipe, cy),
                size = Size(pipeThickness, size.height - cy)
            )
            Direction.LEFT -> drawRect(
                color = boardTheme.pipeBody,
                topLeft = Offset(0f, cy - halfPipe),
                size = Size(cx, pipeThickness)
            )
            Direction.RIGHT -> drawRect(
                color = boardTheme.pipeBody,
                topLeft = Offset(cx, cy - halfPipe),
                size = Size(size.width - cx, pipeThickness)
            )
        }
    }

    // Central pipe joint body
    drawCircle(
        color = boardTheme.pipeBody,
        radius = halfPipe,
        center = Offset(cx, cy)
    )

    // 2. Draw dark inner fluid channels
    val halfChannel = channelThickness / 2f
    for (dir in connections) {
        when (dir) {
            Direction.UP -> drawRect(
                color = boardTheme.pipeCore,
                topLeft = Offset(cx - halfChannel, 0f),
                size = Size(channelThickness, cy)
            )
            Direction.DOWN -> drawRect(
                color = boardTheme.pipeCore,
                topLeft = Offset(cx - halfChannel, cy),
                size = Size(channelThickness, size.height - cy)
            )
            Direction.LEFT -> drawRect(
                color = boardTheme.pipeCore,
                topLeft = Offset(0f, cy - halfChannel),
                size = Size(cx, channelThickness)
            )
            Direction.RIGHT -> drawRect(
                color = boardTheme.pipeCore,
                topLeft = Offset(cx, cy - halfChannel),
                size = Size(size.width - cx, channelThickness)
            )
        }
    }

    // Central dark channel hub
    drawCircle(
        color = boardTheme.pipeCore,
        radius = halfChannel,
        center = Offset(cx, cy)
    )

    // 3. Draw metallic edge flanges at connection exits
    val flangeThickness = size.width * 0.06f
    val flangeWidth = pipeThickness * 1.15f
    val halfFlange = flangeWidth / 2f

    for (dir in connections) {
        when (dir) {
            Direction.UP -> drawRect(
                color = boardTheme.pipeHighlight,
                topLeft = Offset(cx - halfFlange, 0f),
                size = Size(flangeWidth, flangeThickness)
            )
            Direction.DOWN -> drawRect(
                color = boardTheme.pipeHighlight,
                topLeft = Offset(cx - halfFlange, size.height - flangeThickness),
                size = Size(flangeWidth, flangeThickness)
            )
            Direction.LEFT -> drawRect(
                color = boardTheme.pipeHighlight,
                topLeft = Offset(0f, cy - halfFlange),
                size = Size(flangeThickness, flangeWidth)
            )
            Direction.RIGHT -> drawRect(
                color = boardTheme.pipeHighlight,
                topLeft = Offset(size.width - flangeThickness, cy - halfFlange),
                size = Size(flangeThickness, flangeWidth)
            )
        }
    }

    // 4. If connected to source, draw illuminated flow stream!
    if (isConnected) {
        val pulseAlpha = 0.75f + (flowPulsePhase * 0.25f)
        val glowWidth = channelThickness * 0.75f
        val coreWidth = channelThickness * 0.35f

        // Outer glow
        val glowColor = boardTheme.flowGlow.copy(alpha = pulseAlpha * 0.6f)
        for (dir in connections) {
            val endPoint = when (dir) {
                Direction.UP -> Offset(cx, 0f)
                Direction.DOWN -> Offset(cx, size.height)
                Direction.LEFT -> Offset(0f, cy)
                Direction.RIGHT -> Offset(size.width, cy)
            }
            drawLine(
                color = glowColor,
                start = Offset(cx, cy),
                end = endPoint,
                strokeWidth = glowWidth,
                cap = StrokeCap.Round
            )
        }
        drawCircle(
            color = glowColor,
            radius = glowWidth / 2f,
            center = Offset(cx, cy)
        )

        // Bright high-energy inner core
        val coreColor = boardTheme.flowCore.copy(alpha = pulseAlpha)
        for (dir in connections) {
            val endPoint = when (dir) {
                Direction.UP -> Offset(cx, 0f)
                Direction.DOWN -> Offset(cx, size.height)
                Direction.LEFT -> Offset(0f, cy)
                Direction.RIGHT -> Offset(size.width, cy)
            }
            drawLine(
                color = coreColor,
                start = Offset(cx, cy),
                end = endPoint,
                strokeWidth = coreWidth,
                cap = StrokeCap.Round
            )
        }
        drawCircle(
            color = coreColor,
            radius = coreWidth / 2f,
            center = Offset(cx, cy)
        )
    }
}
