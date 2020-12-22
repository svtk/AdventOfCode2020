package day15.part2

import util.readDayInput
import util.readSampleInput

data class NumberInfo(
    val last: Int,
    val lastIndex: Int,
    val positions: MutableMap<Int, Int>
)

fun main() {
    val startingNumbers = readDayInput("day15").first().split(",").map { it.toInt() }
    println(startingNumbers)
    val initialPositions = startingNumbers
        .subList(0, startingNumbers.lastIndex)
        .withIndex()
        .associate { it.value to it.index }
    val initialInfo = NumberInfo(
        startingNumbers.last(),
        startingNumbers.lastIndex,
        initialPositions.toMutableMap()
    )
//    val N = 2020
    val N = 30000000
    val (result, _) =
        (1..N - startingNumbers.size).fold(initialInfo) { (last, lastIndex, positions), _ ->
            val newNumber = if (last in positions) {
                lastIndex - positions.getValue(last)
            } else {
                0
            }
            positions[last] = lastIndex
            NumberInfo(newNumber, lastIndex + 1, positions)
        }
    println(result)
}
