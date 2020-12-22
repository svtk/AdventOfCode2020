package day11

import day11.Seat.*
import util.readDayInput
import util.readSampleInput

val GET_NEIGHBORS_STRATEGY = SeatingSystem::getVisibleSeats
const val NEIGHBORS_LIMIT = 5

enum class Seat(val value: Char) {
    FLOOR('.'), EMPTY('L'), OCCUPIED('#');

    override fun toString() = value.toString()
}

fun Char.toSeat() = when (this) {
    '.' -> FLOOR
    'L' -> EMPTY
    '#' -> OCCUPIED
    else -> throw IllegalArgumentException("Wrong seat value: $this")
}

data class SeatingSystem(val seats: List<List<Seat>>) {
    val height: Int by lazy {
        seats.size
    }
    val width: Int by lazy {
        seats.first().size
    }

    override fun toString() =
        seats.joinToString("\n") { row -> row.joinToString("") }
}

fun SeatingSystem.getNeighbours(i: Int, j: Int): List<Seat> {
    val neighborIndices = listOf(
        Pair(i - 1, j - 1),
        Pair(i, j - 1),
        Pair(i + 1, j - 1),
        Pair(i - 1, j),
        Pair(i + 1, j),
        Pair(i - 1, j + 1),
        Pair(i, j + 1),
        Pair(i + 1, j + 1),
    )
    return neighborIndices.mapNotNull { seats.getOrNull(it.first)?.getOrNull(it.second) }
}

fun SeatingSystem.applyChanges(): SeatingSystem {
    val newSeats = List(seats.size) { seats[it].toMutableList() }
    for (i in 0 until height) {
        for (j in 0 until width) {
            newSeats[i][j] = findNewSeatState(i, j)
        }
    }
    return SeatingSystem((newSeats))
}

fun SeatingSystem.findNewSeatState(
    i: Int,
    j: Int,
): Seat {
    val old = seats[i][j]
    if (old == FLOOR) return old
    val neighbours = GET_NEIGHBORS_STRATEGY(this, i, j)
    return when {
        neighbours.none { it == OCCUPIED } -> OCCUPIED
        neighbours.count { it == OCCUPIED } >= NEIGHBORS_LIMIT -> EMPTY
        else -> old
    }
}

fun SeatingSystem.getVisibleSeats(i: Int, j: Int): List<Seat> {
    val visibleNeighborGetters = listOf( 
        { k: Int -> Pair(i - k, j - k) }, 
        { k: Int -> Pair(i, j - k) }, 
        { k: Int -> Pair(i + k, j - k) }, 
        { k: Int -> Pair(i - k, j) }, 
        { k: Int -> Pair(i + k, j) }, 
        { k: Int -> Pair(i - k, j + k) }, 
        { k: Int -> Pair(i, j + k) }, 
        { k: Int -> Pair(i + k, j + k) }, 
    )
    return visibleNeighborGetters.mapNotNull {
        visibleNeighborGetter -> // { k: Int -> Pair(i - k, j - k) }
        (1..height + width).asSequence().mapNotNull { k ->
            val (first, second) = visibleNeighborGetter(k)
            seats.getOrNull(first)?.getOrNull(second)?.takeIf { it != FLOOR }
        }.firstOrNull()
    }
}

fun stabilizeChanges(seatingSystem: SeatingSystem): SeatingSystem {
    tailrec fun stabilize(seatingSystem: SeatingSystem, stabilized: Boolean): SeatingSystem {
        if (stabilized) return seatingSystem
        val newSystem = seatingSystem.applyChanges()
        println(seatingSystem)
        println()
        return if (newSystem == seatingSystem) stabilize(seatingSystem, true)
        else stabilize(newSystem, false)
    }
    return stabilize(seatingSystem, false)
}

fun main() {
    val initialSystem = SeatingSystem(
        readDayInput("day11").map { s -> s.map(Char::toSeat) }
    )
    val finalSystem = stabilizeChanges(initialSystem)
    println(finalSystem)
    println(finalSystem.seats.flatten().count { it == OCCUPIED })
}