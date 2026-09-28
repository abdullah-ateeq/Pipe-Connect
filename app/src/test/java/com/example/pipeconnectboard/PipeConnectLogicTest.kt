package com.example.pipeconnectboard

import com.example.pipeconnectboard.logic.ConnectivitySolver
import com.example.pipeconnectboard.logic.PuzzleFactory
import com.example.pipeconnectboard.model.Direction
import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeConnectState
import com.example.pipeconnectboard.model.PipeKind
import com.example.pipeconnectboard.model.PipeTile
import com.example.pipeconnectboard.model.getConnections
import com.example.pipeconnectboard.model.opposite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PipeConnectLogicTest {

    @Test
    fun testStraightConnection() {
        val straight0 = PipeTile(1, PipeKind.STRAIGHT, 0)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), getConnections(straight0))

        val straight90 = PipeTile(2, PipeKind.STRAIGHT, 90)
        assertEquals(setOf(Direction.UP, Direction.DOWN), getConnections(straight90))

        val straight180 = PipeTile(3, PipeKind.STRAIGHT, 180)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), getConnections(straight180))

        val straight270 = PipeTile(4, PipeKind.STRAIGHT, 270)
        assertEquals(setOf(Direction.UP, Direction.DOWN), getConnections(straight270))
    }

    @Test
    fun testElbowRotation() {
        val elbow0 = PipeTile(1, PipeKind.ELBOW, 0)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), elbow0.connections)

        val elbow90 = elbow0.rotatedClockwise()
        assertEquals(90, elbow90.rotation)
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN), elbow90.connections)

        val elbow180 = elbow90.rotatedClockwise()
        assertEquals(180, elbow180.rotation)
        assertEquals(setOf(Direction.DOWN, Direction.LEFT), elbow180.connections)

        val elbow270 = elbow180.rotatedClockwise()
        assertEquals(270, elbow270.rotation)
        assertEquals(setOf(Direction.LEFT, Direction.UP), elbow270.connections)

        val elbow360 = elbow270.rotatedClockwise()
        assertEquals(0, elbow360.rotation)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), elbow360.connections)
    }

    @Test
    fun testTJunctionRotation() {
        val t0 = PipeTile(1, PipeKind.T_JUNCTION, 0)
        assertEquals(setOf(Direction.UP, Direction.LEFT, Direction.RIGHT), t0.connections)

        val t90 = t0.rotatedClockwise()
        assertEquals(setOf(Direction.UP, Direction.RIGHT, Direction.DOWN), t90.connections)

        val t180 = t90.rotatedClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN, Direction.LEFT), t180.connections)

        val t270 = t180.rotatedClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.LEFT, Direction.UP), t270.connections)
    }

    @Test
    fun testCrossConnection() {
        val cross0 = PipeTile(1, PipeKind.CROSS, 0)
        val allFour = setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        assertEquals(allFour, cross0.connections)

        val cross90 = cross0.rotatedClockwise()
        assertEquals(allFour, cross90.connections)
    }

    @Test
    fun testOppositeDirections() {
        assertEquals(Direction.DOWN, opposite(Direction.UP))
        assertEquals(Direction.UP, opposite(Direction.DOWN))
        assertEquals(Direction.LEFT, opposite(Direction.RIGHT))
        assertEquals(Direction.RIGHT, opposite(Direction.LEFT))
    }

    @Test
    fun testMatchedNeighborOpenings() {
        // Cell A at (2, 1) has RIGHT, Cell B at (2, 2) has LEFT -> Matched
        val cellA = PipeTile(1, PipeKind.STRAIGHT, 0) // LEFT, RIGHT
        val cellB = PipeTile(2, PipeKind.STRAIGHT, 0) // LEFT, RIGHT

        assertTrue(cellA.connections.contains(Direction.RIGHT))
        assertTrue(cellB.connections.contains(opposite(Direction.RIGHT)))
    }

    @Test
    fun testMismatchedNeighborOpenings() {
        // Cell A at (2, 1) has RIGHT, Cell B at (2, 2) has UP, DOWN -> Mismatched
        val cellA = PipeTile(1, PipeKind.STRAIGHT, 0) // LEFT, RIGHT
        val cellB = PipeTile(2, PipeKind.STRAIGHT, 90) // UP, DOWN

        assertTrue(cellA.connections.contains(Direction.RIGHT))
        assertFalse(cellB.connections.contains(opposite(Direction.RIGHT)))
    }

    @Test
    fun testSourceConnection() {
        val board = PuzzleFactory.createInitialBoard().map { it.toMutableList() }.toMutableList()
        // Source is at (2, 0) and connects RIGHT
        val initialConnected = ConnectivitySolver.findConnectedFromSource(board, GridPosition(2, 0))
        assertEquals(setOf(GridPosition(2, 0)), initialConnected)

        // Placing a straight horizontal pipe at (2, 1) connects to source
        board[2][1] = PipeCell.Pipe(PipeTile(1, PipeKind.STRAIGHT, 0))
        val updatedConnected = ConnectivitySolver.findConnectedFromSource(board, GridPosition(2, 0))
        assertTrue(updatedConnected.contains(GridPosition(2, 1)))

        // Placing a vertical pipe at (2, 1) does NOT connect to source
        board[2][1] = PipeCell.Pipe(PipeTile(2, PipeKind.STRAIGHT, 90))
        val verticalConnected = ConnectivitySolver.findConnectedFromSource(board, GridPosition(2, 0))
        assertFalse(verticalConnected.contains(GridPosition(2, 1)))
    }

    @Test
    fun testDestinationConnection() {
        // Destination is at (3, 5) and accepts connection from LEFT
        assertEquals(setOf(Direction.LEFT), PipeCell.Destination.connections())
    }

    @Test
    fun testBlockedPlacementRejection() {
        val initialBoard = PuzzleFactory.createInitialBoard()
        // Blocked cells cannot be placed upon
        PuzzleFactory.BLOCKED_POSITIONS.forEach { pos ->
            val cell = initialBoard[pos.row][pos.col]
            assertTrue(cell is PipeCell.Blocked)
            assertFalse(cell.isPlaceable)
        }
        // Source and destination are not placeable
        assertFalse(initialBoard[2][0].isPlaceable)
        assertFalse(initialBoard[3][5].isPlaceable)
        // Empty cells are placeable
        assertTrue(initialBoard[0][0].isPlaceable)
    }

    @Test
    fun testSuccessfulSourceToDestinationPath() {
        val board = PuzzleFactory.createInitialBoard().map { it.toMutableList() }.toMutableList()
        val src = GridPosition(2, 0)
        val dst = GridPosition(3, 5)

        // Path from (2, 0) to (3, 5):
        // (2, 1): Straight (LEFT, RIGHT)
        // (2, 2): Straight (LEFT, RIGHT)
        // (2, 3): Elbow (LEFT, DOWN) -> rotation 180 (DOWN, LEFT)
        // (3, 3): Elbow (UP, RIGHT) -> rotation 0 (UP, RIGHT)
        // (3, 4): Straight (LEFT, RIGHT) -> rotation 0 (LEFT, RIGHT)
        // (3, 5): Destination (LEFT)

        board[2][1] = PipeCell.Pipe(PipeTile(1, PipeKind.STRAIGHT, 0))
        board[2][2] = PipeCell.Pipe(PipeTile(2, PipeKind.STRAIGHT, 0))
        board[2][3] = PipeCell.Pipe(PipeTile(3, PipeKind.ELBOW, 180))
        board[3][3] = PipeCell.Pipe(PipeTile(4, PipeKind.ELBOW, 0))
        board[3][4] = PipeCell.Pipe(PipeTile(5, PipeKind.STRAIGHT, 0))

        val connected = ConnectivitySolver.findConnectedFromSource(board, src)
        assertTrue(connected.contains(src))
        assertTrue(connected.contains(GridPosition(2, 1)))
        assertTrue(connected.contains(GridPosition(2, 2)))
        assertTrue(connected.contains(GridPosition(2, 3)))
        assertTrue(connected.contains(GridPosition(3, 3)))
        assertTrue(connected.contains(GridPosition(3, 4)))
        assertTrue(connected.contains(dst))

        assertTrue(ConnectivitySolver.isDestinationConnected(connected, dst))
    }

    @Test
    fun testDisconnectedPipesDoNotWin() {
        val board = PuzzleFactory.createInitialBoard().map { it.toMutableList() }.toMutableList()
        val src = GridPosition(2, 0)
        val dst = GridPosition(3, 5)

        // Place a pipe right next to destination, but not connected to source
        board[3][4] = PipeCell.Pipe(PipeTile(1, PipeKind.STRAIGHT, 0))

        val connected = ConnectivitySolver.findConnectedFromSource(board, src)
        assertFalse(connected.contains(GridPosition(3, 4)))
        assertFalse(connected.contains(dst))
        assertFalse(ConnectivitySolver.isDestinationConnected(connected, dst))
    }

    @Test
    fun testLoopHandling() {
        val board = PuzzleFactory.createInitialBoard().map { it.toMutableList() }.toMutableList()
        val src = GridPosition(2, 0)

        // Create a 2x2 loop connected to source:
        // (2, 1): T-junction (UP, LEFT, RIGHT) connects to source (2, 0)
        // (1, 1): Elbow (RIGHT, DOWN) -> 90 deg
        // (1, 2 is blocked, so let's loop (2,1)-(2,2)-(3,2)-(3,1)):
        // (2, 1): Cross
        // (2, 2): Elbow (DOWN, LEFT) -> 180
        // (3, 2): Elbow (UP, LEFT) -> 270
        // (3, 1): Elbow (UP, RIGHT) -> 0
        board[2][1] = PipeCell.Pipe(PipeTile(1, PipeKind.CROSS, 0))
        board[2][2] = PipeCell.Pipe(PipeTile(2, PipeKind.ELBOW, 180))
        board[3][2] = PipeCell.Pipe(PipeTile(3, PipeKind.ELBOW, 270))
        board[3][1] = PipeCell.Pipe(PipeTile(4, PipeKind.ELBOW, 0))

        // BFS must complete without infinite recursion
        val connected = ConnectivitySolver.findConnectedFromSource(board, src)
        assertTrue(connected.contains(GridPosition(2, 1)))
        assertTrue(connected.contains(GridPosition(2, 2)))
        assertTrue(connected.contains(GridPosition(3, 2)))
        assertTrue(connected.contains(GridPosition(3, 1)))
    }

    @Test
    fun testNeighborOutOfBounds() {
        val topLeft = GridPosition(0, 0)
        assertNull(topLeft.neighbor(Direction.UP))
        assertNull(topLeft.neighbor(Direction.LEFT))
        assertEquals(GridPosition(0, 1), topLeft.neighbor(Direction.RIGHT))
        assertEquals(GridPosition(1, 0), topLeft.neighbor(Direction.DOWN))

        val bottomRight = GridPosition(5, 5)
        assertNull(bottomRight.neighbor(Direction.DOWN))
        assertNull(bottomRight.neighbor(Direction.RIGHT))
        assertEquals(GridPosition(5, 4), bottomRight.neighbor(Direction.LEFT))
        assertEquals(GridPosition(4, 5), bottomRight.neighbor(Direction.UP))
    }

    @Test
    fun testTileExhaustionFailureLogic() {
        var state = PuzzleFactory.createInitialState()
        // Check initial state
        assertEquals(3, state.tray.size)
        assertEquals(15, state.bag.size)
        assertEquals(18, state.pipesRemaining)
        assertFalse(state.isComplete)
        assertFalse(state.isFailed)

        // Exhaust tray and bag without connecting destination
        state = state.copy(tray = emptyList(), bag = emptyList())
        val isExhausted = state.tray.isEmpty() && state.bag.isEmpty() && !state.isComplete
        assertTrue(isExhausted)
    }

    @Test
    fun testUndoRestoresFullState() {
        val initial = PuzzleFactory.createInitialState()
        val undoStack = ArrayDeque<PipeConnectState>()
        undoStack.addLast(initial)

        // Simulate a move
        val modifiedBoard = initial.board.map { it.toMutableList() }
        modifiedBoard[2][1] = PipeCell.Pipe(initial.tray[0])
        val afterMove = initial.copy(
            board = modifiedBoard,
            tray = initial.tray.drop(1),
            moveCount = 1
        )

        // Undo
        val restored = undoStack.removeLast()
        assertEquals(0, restored.moveCount)
        assertEquals(3, restored.tray.size)
        assertTrue(restored.board[2][1] is PipeCell.Empty)
    }
}
