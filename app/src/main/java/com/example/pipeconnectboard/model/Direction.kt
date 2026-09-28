package com.example.pipeconnectboard.model

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    fun rotateCw(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    fun rotate(degrees: Int): Direction {
        val steps = ((degrees % 360 + 360) % 360) / 90
        var dir = this
        repeat(steps) {
            dir = dir.rotateCw()
        }
        return dir
    }
}

fun opposite(direction: Direction): Direction = direction.opposite()
