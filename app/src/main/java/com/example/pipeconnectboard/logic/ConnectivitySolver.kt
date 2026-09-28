package com.example.pipeconnectboard.logic

import com.example.pipeconnectboard.model.Direction
import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.getCellConnections

object ConnectivitySolver {

    /**
     * Traverses the board starting from [sourcePosition] using BFS.
     * Follows only matching adjacent pipe connections:
     * Current cell must have direction D, and neighbor cell must have opposite(D).
     */
    fun findConnectedFromSource(
        board: List<List<PipeCell>>,
        sourcePosition: GridPosition = GridPosition(2, 0)
    ): Set<GridPosition> {
        val visited = mutableSetOf<GridPosition>()
        val queue = ArrayDeque<GridPosition>()

        // Ensure source exists and is valid
        if (sourcePosition.row !in board.indices || sourcePosition.col !in board[sourcePosition.row].indices) {
            return emptySet()
        }

        visited.add(sourcePosition)
        queue.add(sourcePosition)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentConnections = getCellConnections(board, current)

            for (direction in currentConnections) {
                val neighborPos = current.neighbor(direction) ?: continue

                // Check neighbor validity and whether already visited (handles loops safely)
                if (neighborPos !in visited) {
                    val neighborConnections = getCellConnections(board, neighborPos)
                    if (neighborConnections.contains(direction.opposite())) {
                        visited.add(neighborPos)
                        queue.add(neighborPos)
                    }
                }
            }
        }

        return visited
    }

    /**
     * Checks if the destination position is reached from source.
     */
    fun isDestinationConnected(
        connectedCells: Set<GridPosition>,
        destinationPosition: GridPosition = GridPosition(3, 5)
    ): Boolean {
        return connectedCells.contains(destinationPosition)
    }
}
