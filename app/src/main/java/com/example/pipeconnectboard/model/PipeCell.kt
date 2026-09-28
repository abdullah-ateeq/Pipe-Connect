package com.example.pipeconnectboard.model

sealed class PipeCell {
    data object Empty : PipeCell()
    data object Blocked : PipeCell()
    data object Source : PipeCell()
    data object Destination : PipeCell()
    data class Pipe(val tile: PipeTile) : PipeCell()

    fun connections(): Set<Direction> {
        return when (this) {
            is Empty -> emptySet()
            is Blocked -> emptySet()
            is Source -> setOf(Direction.RIGHT)
            is Destination -> setOf(Direction.LEFT)
            is Pipe -> tile.connections
        }
    }

    val isPlaceable: Boolean
        get() = this is Empty
}

fun getCellConnections(cell: PipeCell): Set<Direction> = cell.connections()

fun getCellConnections(
    board: List<List<PipeCell>>,
    position: GridPosition
): Set<Direction> {
    if (position.row !in board.indices || position.col !in board[position.row].indices) {
        return emptySet()
    }
    return board[position.row][position.col].connections()
}
