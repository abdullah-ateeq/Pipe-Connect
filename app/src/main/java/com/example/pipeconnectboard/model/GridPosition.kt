package com.example.pipeconnectboard.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun neighbor(direction: Direction, numRows: Int = 6, numCols: Int = 6): GridPosition? {
        val nextRow = when (direction) {
            Direction.UP -> row - 1
            Direction.DOWN -> row + 1
            Direction.LEFT, Direction.RIGHT -> row
        }
        val nextCol = when (direction) {
            Direction.LEFT -> col - 1
            Direction.RIGHT -> col + 1
            Direction.UP, Direction.DOWN -> col
        }

        return if (nextRow in 0 until numRows && nextCol in 0 until numCols) {
            GridPosition(nextRow, nextCol)
        } else {
            null
        }
    }
}

fun neighbor(
    position: GridPosition,
    direction: Direction
): GridPosition? = position.neighbor(direction)
