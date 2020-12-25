package day23.part2

import util.readSampleInput

val DEBUG = false

fun main() {
    val size = 9
    val moves = 10
//    val size = 1000000
//    val moves = 1000//0000
    val initialInput = readSampleInput("day23").first().map { it - '0' }
    val max = initialInput.maxOrNull()!!
    val initialArray = (initialInput + ((max + 1)..size).toList()).toIntArray()
    val cups = Cups(move = 1, initialArray, IntArray(initialArray.size))
    repeat(moves) {
        cups.nextMove()
    }
    log(true) { cups.values.joinToString() }
    println(cups.resultPart1())
    println(cups.resultPart2())
}

fun Cups.resultPart1(): String {
    val oneIndex = values.indexOf(1)
    return (1..values.lastIndex).joinToString("") { "${this[oneIndex + it]}" }
}


fun Cups.resultPart2(): Long {
    val oneIndex = values.indexOf(1)
    return 1L * this[oneIndex + 1] * this[oneIndex + 2]
}

class Cups(
    var move: Int,
    var values: IntArray,
    var next: IntArray,
) {
    operator fun get(i: Int) = values.getNormalized(i)
}

fun IntArray.normalizeIndex(i: Int) = i % size
fun IntArray.getNormalized(i: Int) = this[normalizeIndex(i)]

fun Cups.nextMove() {
    log { "-- move $move --" }
    log {
        "cups: " + values.withIndex().joinToString("") { (index, value) ->
            if (index == 0) "($value)" else " $value "
        }
    }
    log {
        val pickedUp = (1..3).map { this[it] }
        "pick up: " + pickedUp.joinToString()
    }
    val currentCup = this[0]
    val circleIndices = 4..values.lastIndex
    val destinationCupIndex = circleIndices
        .filter { this[it] < currentCup }
        .maxByOrNull { this[it] }
        ?: circleIndices.maxByOrNull { this[it] }!!

    log {
        val destination = this[destinationCupIndex]
        "destination: $destination"
    }
    log {
        "destination index: $destinationCupIndex"
    }
    log()
    for (i in 4..destinationCupIndex) {
        next[i - 4] = values[i]
    }
    val newDestinationIndex = destinationCupIndex - 4
    for (i in 1..3) {
        next[newDestinationIndex + i] = values[i]
    }
    for (i in (destinationCupIndex + 1)..values.lastIndex) {
        next[i - 1] = values[i]
    }
    next[values.lastIndex] = values[0]

    val tmp = values
    values = next
    next = tmp
    move++
}

fun log(message: Any? = "", debug: Boolean = DEBUG) {
    if (debug) println(message)
}

inline fun log(debug: Boolean = DEBUG, message: () -> Any?) {
    if (debug) {
        println(message())
    }
}