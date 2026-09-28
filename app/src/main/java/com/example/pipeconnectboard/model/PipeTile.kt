package com.example.pipeconnectboard.model

data class PipeTile(
    val id: Int,
    val kind: PipeKind,
    val rotation: Int = 0
) {
    init {
        require(rotation in setOf(0, 90, 180, 270)) { "Rotation must be normalized to 0, 90, 180, or 270" }
    }

    val connections: Set<Direction>
        get() = getConnections(this)

    fun rotatedClockwise(): PipeTile {
        val nextRotation = (rotation + 90) % 360
        return copy(rotation = nextRotation)
    }

    fun accessibilityDescription(): String {
        val rotDesc = when (rotation) {
            0 -> "default orientation"
            90 -> "rotated 90 degrees"
            180 -> "rotated 180 degrees"
            270 -> "rotated 270 degrees"
            else -> "$rotation degrees"
        }
        val detail = when (kind) {
            PipeKind.STRAIGHT -> if (rotation % 180 == 0) "horizontal" else "vertical"
            PipeKind.ELBOW -> "corner"
            PipeKind.T_JUNCTION -> "three-way"
            PipeKind.CROSS -> "four-way"
        }
        return "${kind.label} pipe, $detail, $rotDesc"
    }
}

fun getConnections(tile: PipeTile): Set<Direction> {
    val base = tile.kind.baseConnections()
    if (tile.rotation == 0) return base
    return base.map { it.rotate(tile.rotation) }.toSet()
}
