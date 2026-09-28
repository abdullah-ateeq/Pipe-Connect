package com.example.pipeconnectboard.logic

import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeConnectState
import com.example.pipeconnectboard.model.PipeKind
import com.example.pipeconnectboard.model.PipeTile

object PuzzleFactory {

    const val GRID_SIZE = 6
    val SOURCE_POS = GridPosition(2, 0)
    val DESTINATION_POS = GridPosition(3, 5)

    // 4 balanced, strategic blocked cells
    val BLOCKED_POSITIONS = setOf(
        GridPosition(1, 2),
        GridPosition(1, 4),
        GridPosition(4, 1),
        GridPosition(4, 3)
    )

    /**
     * Creates the initial board layout:
     * - Fixed Source at (2, 0)
     * - Fixed Destination at (3, 5)
     * - Fixed Blocked cells at (1, 2), (1, 4), (4, 1), (4, 3)
     * - All other cells Empty
     */
    fun createInitialBoard(): List<List<PipeCell>> {
        return List(GRID_SIZE) { r ->
            List(GRID_SIZE) { c ->
                val pos = GridPosition(r, c)
                when {
                    pos == SOURCE_POS -> PipeCell.Source
                    pos == DESTINATION_POS -> PipeCell.Destination
                    BLOCKED_POSITIONS.contains(pos) -> PipeCell.Blocked
                    else -> PipeCell.Empty
                }
            }
        }
    }

    /**
     * Fixed deterministic tile supply totaling 18 tiles:
     * 6 Straight, 6 Elbow, 4 T-Junction, 2 Cross
     * First 3 are drawn for the tray, remaining 15 remain in the bag.
     */
    fun createTileSupply(): Pair<List<PipeTile>, List<PipeTile>> {
        var nextId = 1

        val allTiles = listOf(
            // Initial 3 tray tiles (broad strategic choices right away)
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.T_JUNCTION),

            // Remaining 15 tiles in deterministic sequence
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.CROSS),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.T_JUNCTION),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.CROSS),
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.T_JUNCTION),
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.STRAIGHT),
            PipeTile(id = nextId++, kind = PipeKind.ELBOW),
            PipeTile(id = nextId++, kind = PipeKind.T_JUNCTION)
        )

        val tray = allTiles.take(3)
        val bag = allTiles.drop(3)
        return Pair(tray, bag)
    }

    /**
     * Creates a new fresh game state.
     */
    fun createInitialState(): PipeConnectState {
        val board = createInitialBoard()
        val (tray, bag) = createTileSupply()
        val connectedCells = ConnectivitySolver.findConnectedFromSource(board, SOURCE_POS)

        return PipeConnectState(
            board = board,
            tray = tray,
            bag = bag,
            selectedTileId = tray.firstOrNull()?.id,
            moveCount = 0,
            connectedCells = connectedCells,
            isComplete = false,
            isFailed = false,
            elapsedMillis = 0L,
            isAnimating = false,
            totalStartingPipes = 18
        )
    }
}
