package day10

import util.readDayInput
import util.readSampleInput
import java.math.BigInteger
import kotlin.math.pow

fun main() {
    val jolts = readSampleInput("day10")
        .map { it.toInt() }
        .sorted()
        .let { list -> listOf(0) + list + (list.maxOrNull()!! + 3) }
    println(jolts)
    val differences = jolts
        .windowed(2)
        .also { println(it) }
        .map { (first, second) -> second - first }
        .also { println(it) }

    println(differences)
//    countDifferences(differences)
    val chunks = differences.fold(mutableListOf<MutableList<Int>>()) {
        list, newElement ->
        if (list.lastOrNull()?.lastOrNull() == newElement) {
            list.last() += newElement
        } else {
            list += mutableListOf(newElement)
        }
        list
    }
    println(chunks)
    println(chunks.maxOf { it.size })
    fun countChunks(size: Int) = chunks.count { it.first() == 1 && it.size == size }
    val two = BigInteger.valueOf(2)
    val four = BigInteger.valueOf(4)
    val seven = BigInteger.valueOf(7)
    println(two.pow(countChunks(2)) * four.pow(countChunks(3)) * seven.pow(countChunks(4)))
    println(countChunks(2)) // 2
    println(countChunks(3)) // 2 * 2
    println(countChunks(4)) // 7 * 7 * 7 * 7
}
/*
19208 = 2 * 2 * 2 * 7 * 7 * 7 * 7
 */

private fun countDifferences(differences: List<Int>) {
    val ones = differences.count { it == 1 }
    val threes = differences.count { it == 3 }
    println(ones)
    println(threes)
    println(ones * threes)
}

/*
     1, 3,[1][1] 1,  3, [1]  1,  3,  1,  3,   3
(0), 1, 4, 5, 6, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 5, 6, 7, 10,     12, 15, 16, 19, (22)
(0), 1, 4, 5,    7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4, 5,    7, 10,     12, 15, 16, 19, (22)
(0), 1, 4,    6, 7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4,    6, 7, 10,     12, 15, 16, 19, (22)
(0), 1, 4,       7, 10, 11, 12, 15, 16, 19, (22)
(0), 1, 4,       7, 10,     12, 15, 16, 19, (22)
 */