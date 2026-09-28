package com.example.pipeconnectboard.model

enum class PipeKind(val label: String) {
    STRAIGHT("Straight"),
    ELBOW("Elbow"),
    T_JUNCTION("T-Junction"),
    CROSS("Cross");

    fun baseConnections(): Set<Direction> {
        return when (this) {
            STRAIGHT -> setOf(Direction.LEFT, Direction.RIGHT)
            ELBOW -> setOf(Direction.UP, Direction.RIGHT)
            T_JUNCTION -> setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            CROSS -> setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        }
    }
}
