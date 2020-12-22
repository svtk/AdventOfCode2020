package day17

import day17.State.*
import util.readDayInput
import util.readSampleInput

fun main() {
    val input = readDayInput("day17")
    val halfSize = input.size / 2
    val initialCube = HyperCube(halfSize, 0)
    for ((x, line) in input.withIndex()) {
        for ((y, char) in line.withIndex()) {
            initialCube[x - halfSize, y - halfSize, 0, 0] = char.toState()
        }
    }
    val result = (1..6).fold(initialCube) { cube, _ -> nextCycle(cube) }
    println(initialCube)
    println("After 6 cycle")
    println(result.countActive())
}

data class HyperCube(val halfWidth: Int, val halfDepth: Int) {
    private val fullWidth = 2 * halfWidth + 1
    private val fullDepth = 2 * halfWidth + 1
    private val content: List<List<List<MutableList<State>>>> =
        List(fullWidth) { List(fullWidth) { List(fullDepth) { MutableList(fullDepth) { INACTIVE } } } }

    val widthIndices get() = -halfWidth..halfWidth
    val depthIndices get() = -halfDepth..halfDepth

    operator fun get(x: Int, y: Int, z: Int, w: Int): State =
        content
            .getOrNull(x + halfWidth)
            ?.getOrNull(y + halfWidth)
            ?.getOrNull(z + halfDepth)
            ?.getOrNull(w + halfDepth)
            ?: INACTIVE

    operator fun set(x: Int, y: Int, z: Int, w: Int, state: State) {
        content[x + halfWidth][y + halfWidth][z + halfDepth][w + halfDepth] = state
    }

    override fun toString() = buildString {
        for (w in depthIndices) {
            for (z in depthIndices) {
                appendLine("z=$z, w = $w")
                for (x in widthIndices) {
                    for (y in widthIndices) {
                        append(this@HyperCube[x, y, z, w].char)
                    }
                    appendLine()
                }
                appendLine()
            }
        }
    }
}

fun HyperCube.countActiveNeighbours(xp: Int, yp: Int, zp: Int, wp: Int): Int {
    var count = 0
    for (x in widthIndices) {
        for (y in widthIndices) {
            for (z in depthIndices) {
                for (w in depthIndices) {
                    if (x in xp - 1..xp + 1 &&
                        y in yp - 1..yp + 1 &&
                        z in zp - 1..zp + 1 &&
                        w in wp - 1..wp + 1 &&
                        !(x == xp && y == yp && z == zp && w == wp)
                    ) {
                        if (this[x, y, z, w] == ACTIVE) {
                            count++
                        }
                    }
                }
            }
        }
    }
    return count
}

fun HyperCube.countActive(): Int {
    var count = 0
    for (x in widthIndices) {
        for (y in widthIndices) {
            for (z in depthIndices) {
                for (w in depthIndices) {
                    if (this[x, y, z, w] == ACTIVE) {
                        count++
                    }
                }
            }
        }
    }
    return count
}

fun nextCycle(oldCube: HyperCube): HyperCube {
    val newCube = HyperCube(oldCube.halfWidth + 1, oldCube.halfDepth + 1)
    for (x in newCube.widthIndices) {
        for (y in newCube.widthIndices) {
            for (z in newCube.depthIndices) {
                for (w in newCube.depthIndices) {
                    val activeNeighboursCount = oldCube.countActiveNeighbours(x, y, z, w)
                    val newState =
                        if (oldCube[x, y, z, w] == ACTIVE && activeNeighboursCount in setOf(2, 3)) {
                            ACTIVE
                        } else if (oldCube[x, y, z, w] == INACTIVE && activeNeighboursCount == 3) {
                            ACTIVE
                        } else {
                            INACTIVE
                        }
                    newCube[x, y, z, w] = newState
                }
            }
        }
    }
    return newCube
}