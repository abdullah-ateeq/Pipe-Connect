package com.example.pipeconnectboard.model

data class PipeConnectState(
    val board: List<List<PipeCell>>,
    val tray: List<PipeTile>,
    val bag: List<PipeTile>,
    val selectedTileId: Int? = null,
    val moveCount: Int = 0,
    val connectedCells: Set<GridPosition> = emptySet(),
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val elapsedMillis: Long = 0L,
    val isAnimating: Boolean = false,
    val totalStartingPipes: Int = 18
) {
    val selectedTile: PipeTile?
        get() = tray.find { it.id == selectedTileId }

    val pipesRemaining: Int
        get() = tray.size + bag.size

    val pipesUsed: Int
        get() = (totalStartingPipes - pipesRemaining).coerceAtLeast(0)

    val isConnected: Boolean
        get() = isComplete || (connectedCells.contains(GridPosition(3, 5)))

    companion object {
        val SOURCE_POSITION = GridPosition(2, 0)
        val DESTINATION_POSITION = GridPosition(3, 5)
    }
}
