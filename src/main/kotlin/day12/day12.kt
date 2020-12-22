package day12

import day12.Action.*
import util.readDayInput
import kotlin.math.absoluteValue

// north, south, east, west, left, right, forward
enum class Action {
    N, S, E, W, L, R, F
}

data class Instruction(val action: Action, val value: Int)

data class Position(
    val direction: Action,
    val x: Int,
    val y: Int,
)

fun turn(direction: Action, degree: Int, right: Boolean): Action {
    fun illegalState(): Nothing =
        throw IllegalStateException("Wrong direction: $direction")

    val sides = listOf(N, E, S, W)
    val step = degree / 90 * (if (right) 1 else -1)
    val currentIndex = sides.indexOf(direction)
        .takeIf { it != -1 }
        ?: illegalState()
    val newIndex = ((currentIndex + step) % sides.size).let {
        if (it < 0) it + sides.size else it
    }
    return sides[newIndex]
}

//F10
//N3
//F7
//R90
//F11

fun String.toInstruction() =
    Instruction(valueOf(substring(0..0)), substring(1..lastIndex).toInt())

tailrec fun calculateNextPosition(current: Position, instruction: Instruction): Position =
    when (instruction.action) {
        N -> current.copy(y = current.y + instruction.value)
        S -> current.copy(y = current.y - instruction.value)
        E -> current.copy(x = current.x + instruction.value)
        W -> current.copy(x = current.x - instruction.value)
        L -> current.copy(direction = turn(current.direction, degree = instruction.value, right = false))
        R -> current.copy(direction = turn(current.direction, degree = instruction.value, right = true))
        F -> calculateNextPosition(current, instruction.copy(action = current.direction))
    }

fun main() {
    val instructions = readDayInput("day12").map(String::toInstruction)
    val initialPosition = Position(E, 0, 0)
    val final = instructions.fold(initialPosition) { position, instruction ->
        calculateNextPosition(position, instruction).also { println(position) }
    }
    println(final)
    println(final.x.absoluteValue + final.y.absoluteValue)
}