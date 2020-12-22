package day12part2

import day12part2.Action.*
import util.readDayInput
import util.readSampleInput
import kotlin.math.absoluteValue

// north, south, east, west, left, right, forward
enum class Action {
    N, S, E, W, L, R, F
}

data class Instruction(val action: Action, val value: Int)

data class Waypoint(val xOffset: Int, val yOffset: Int)

data class Position(
    val waypoint: Waypoint,
    val x: Int,
    val y: Int,
)

fun rotate(initialWaypoint: Waypoint, degree: Int, right: Boolean): Waypoint {
    val step = degree / 90
    return (1..step).fold(initialWaypoint) { waypoint: Waypoint, _ ->
        waypoint.rotate(right)
    }
}

fun Waypoint.rotate(right: Boolean) =
    if (right) Waypoint(yOffset, -xOffset) else Waypoint(-yOffset, xOffset)

//F10
//N3
//F7
//R90
//F11

fun String.toInstruction() =
    Instruction(valueOf(substring(0..0)), substring(1..lastIndex).toInt())

fun Position.moveWaypoint(moveWaypoint: (Waypoint) -> Waypoint) =
    copy(waypoint = moveWaypoint(waypoint))


fun calculateNextPosition(current: Position, instruction: Instruction): Position =
    when (instruction.action) {
        N -> current.moveWaypoint { w -> w.copy(yOffset = w.yOffset + instruction.value) }
        S -> current.moveWaypoint { w -> w.copy(yOffset = w.yOffset - instruction.value) }
        E -> current.moveWaypoint { w -> w.copy(xOffset = w.xOffset + instruction.value) }
        W -> current.moveWaypoint { w -> w.copy(xOffset = w.xOffset - instruction.value) }
        L -> current.copy(waypoint = rotate(current.waypoint, degree = instruction.value, right = false))
        R -> current.copy(waypoint = rotate(current.waypoint, degree = instruction.value, right = true))
        F -> current.moveToViewPoint(instruction.value)
    }

private fun Position.moveToViewPoint(step: Int) = copy(
    x = x + waypoint.xOffset * step,
    y = y + waypoint.yOffset * step
)

fun main() {
    val instructions = readDayInput("day12").map(String::toInstruction)
    val initialPosition = Position(Waypoint(xOffset = 10, yOffset = 1), 0, 0)
    val final = instructions.fold(initialPosition) { position, instruction ->
        calculateNextPosition(position, instruction).also { println(position) }
    }
    println(final)
    println(final.x.absoluteValue + final.y.absoluteValue)
}