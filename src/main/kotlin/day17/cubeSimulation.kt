package day17

import day17.State.*
import util.readDayInput
import util.readSampleInput

fun main() {
    val input = readDayInput("day17")
    val halfSize = input.size / 2
    val initialCube = Cube(halfSize, 0)
    for ((x, line) in input.withIndex()) {
        for ((y, char) in line.withIndex()) {
            initialCube[x - halfSize, y - halfSize, 0] = char.toState()
        }
    }
    val result = (1..6).fold(initialCube) { cube, _ -> nextCycle(cube) }
    println(initialCube)
    println("After 6 cycle")
    println(result.countActive())
}

enum class State(val char: Char) {
    ACTIVE('#'), INACTIVE('.')
}

fun Char.toState() = when (this) {
    '#' -> ACTIVE
    '.' -> INACTIVE
    else -> throw IllegalArgumentException("Wrong state")
}

data class Cube(val halfWidth: Int, val halfDepth: Int) {
    private val fullWidth = 2 * halfWidth + 1
    private val content: List<List<MutableList<State>>> =
        List(fullWidth) { List(fullWidth) { MutableList(2 * halfDepth + 1) { INACTIVE } } }

    val widthIndices get() = -halfWidth..halfWidth
    val depthIndices get() = -halfDepth..halfDepth

    operator fun get(x: Int, y: Int, z: Int): State =
        content.getOrNull(x + halfWidth)?.getOrNull(y + halfWidth)?.getOrNull(z + halfDepth) ?: INACTIVE

    operator fun set(x: Int, y: Int, z: Int, state: State) {
        content[x + halfWidth][y + halfWidth][z + halfDepth] = state
    }

    override fun toString() = buildString {
        for (z in depthIndices) {
            appendLine("z=$z")
            for (x in widthIndices) {
                for (y in widthIndices) {
                    append(this@Cube[x, y, z].char)
                }
                appendLine()
            }
            appendLine()
        }
    }
}

fun Cube.countActiveNeighbours(xp: Int, yp: Int, zp: Int): Int {
    var count = 0
    for (x in widthIndices) {
        for (y in widthIndices) {
            for (z in depthIndices) {
                if (x in xp - 1..xp + 1 && y in yp - 1..yp + 1 && z in zp - 1..zp + 1 &&
                    !(x == xp && y == yp && z == zp)
                ) {
                    if (this[x, y, z] == ACTIVE) {
                        count++
                    }
                }
            }
        }
    }
    return count
}

fun Cube.countActive(): Int {
    var count = 0
    for (x in widthIndices) {
        for (y in widthIndices) {
            for (z in depthIndices) {
                if (this[x, y, z] == ACTIVE) {
                    count++
                }
            }
        }
    }
    return count
}

fun nextCycle(oldCube: Cube): Cube {
    val newCube = Cube(oldCube.halfWidth + 1, oldCube.halfDepth + 1)
    for (x in newCube.widthIndices) {
        for (y in newCube.widthIndices) {
            for (z in newCube.depthIndices) {
                val activeNeighboursCount = oldCube.countActiveNeighbours(x, y, z)
                val newState =
                    if (oldCube[x, y, z] == ACTIVE && activeNeighboursCount in setOf(2, 3)) {
                        ACTIVE
                    } else if (oldCube[x, y, z] == INACTIVE && activeNeighboursCount == 3) {
                        ACTIVE
                    } else {
                        INACTIVE
                    }
                newCube[x, y, z] = newState
            }
        }
    }
    return newCube
}