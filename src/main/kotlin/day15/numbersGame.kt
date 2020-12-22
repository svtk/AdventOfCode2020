package day15

import util.readDayInput
import util.readSampleInput

data class NumberInfo(val list: List<Int>, val positions: Map<Int, Int>)

fun main() {
    val startingNumbers = readDayInput("day15").first().split(",").map { it.toInt() }
    println(startingNumbers)
    val initialPositions = startingNumbers
        .subList(0, startingNumbers.lastIndex)
        .withIndex()
        .associate { it.value to it.index }
    val (resultingList, _) =
        (1..2020).fold(NumberInfo(startingNumbers, initialPositions)) { (list, positions), _ ->
            val last = list.last()
            val lastIndex = list.lastIndex
            val newNumber = if (last in positions) {
                lastIndex - positions.getValue(last)
            } else {
                0
            }
//            println("last: $last positions: $positions new: $newNumber")
            NumberInfo(list + newNumber, positions + (last to lastIndex))
        }
    println(resultingList)
//    println(resultingPositions)
    println(resultingList[2019])
}
