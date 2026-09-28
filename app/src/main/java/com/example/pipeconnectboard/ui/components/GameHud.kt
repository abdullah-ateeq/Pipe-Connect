package com.example.pipeconnectboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pipeconnectboard.ui.theme.AccentEmeraldDestination
import com.example.pipeconnectboard.ui.theme.BorderDark

@Composable
fun GameHud(
    isConnected: Boolean,
    tilesLeft: Int,
    moves: Int,
    elapsedMillis: Long,
    showTimer: Boolean,
    modifier: Modifier = Modifier
) {
    val minutes = (elapsedMillis / 1000) / 60
    val seconds = (elapsedMillis / 1000) % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Connected Status
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "CONNECTED",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (isConnected) AccentEmeraldDestination else Color(0xFF64748B),
                            CircleShape
                        )
                )
                Text(
                    text = if (isConnected) "YES" else "NO",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isConnected) AccentEmeraldDestination else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Tiles Left
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TILES LEFT",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$tilesLeft",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Moves
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MOVES",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$moves",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Time (if enabled)
        if (showTimer) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "TIME",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
