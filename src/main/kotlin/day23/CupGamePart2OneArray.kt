package day23.part2.onearray

import util.readSampleInput

val DEBUG = true

fun main() {
    val size = 9
    val moves = 10
//    val size = 1000000
//    val moves = 100000000
    val input = readSampleInput("day23").first().map { it - '0' }
    val max = input.maxOrNull()!!
    val initialArray = (input + ((max + 1)..size).toList()).toIntArray()
    val cups = Cups(move = 1, currentIndex = 0, initialArray)
    repeat(moves) {
        cups.nextMove()
//        println(cups.resultPart2Numbers())
    }
    log(false) { cups.values.joinToString() }
    println(cups.resultPart1())
    println(cups.resultPart2Numbers())
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

fun Cups.resultPart2Numbers(): Pair<Int, Int> {
    val oneIndex = values.indexOf(1)
    return Pair(this[oneIndex + 1], this[oneIndex + 2])
}

class Cups(
    var move: Int,
    var currentIndex: Int,
    var values: IntArray,
) {
    operator fun get(i: Int) = getNormalized(i)
    fun normalizeIndex(i: Int) = i % values.size
    fun getNormalized(i: Int) = values[normalizeIndex(i)]
}


fun Cups.nextMove() {
    val (p1, p2, p3) = (1..3).map { this[currentIndex + it] }
    val current = this[currentIndex]
    val exclude = (currentIndex..currentIndex + 3).map { normalizeIndex(it) }
    val destinationIndex = values
        .indices
        .filter { it !in exclude && this[it] < current }
        .maxByOrNull { this[it] }
        ?: values
            .indices
            .filter { it !in exclude }
            .maxByOrNull { this[it] }!!

    log { "-- move $move --" }
    log {
        "cups: " + values.withIndex().joinToString("") { (index, value) ->
            when (index) {
                currentIndex -> "($value)"
                destinationIndex -> "[$value]"
                else -> " $value "
            }
        }
    }
    log {
        "pick up: " + listOf(p1, p2, p3).joinToString()
    }
    log {
        val destination = this[destinationIndex]
        "destination: $destination"
    }
    log { "destination index: $destinationIndex" }
    log { "current index: $currentIndex" }

    val lastIndex = values.lastIndex
    val newCurrent = normalizeIndex(currentIndex + 4)

    log { "new current index: $newCurrent" }
    log()
    fun copyStart() {
        values.copyInto(values, destinationOffset = 3, startIndex = 0, endIndex = currentIndex + 1)
    }
    if (destinationIndex < currentIndex) {
        if (newCurrent < destinationIndex) {
            val newValues = (0 until currentIndex - destinationIndex)
                .map { values[normalizeIndex(destinationIndex + 1 + it)] }
            newValues.forEachIndexed { index, value ->
                values[normalizeIndex(destinationIndex + 4 + index)] = value
            }
            values[normalizeIndex(destinationIndex + 1)] = p1
            values[normalizeIndex(destinationIndex + 2)] = p2
            values[normalizeIndex(destinationIndex + 3)] = p3
        } else {
            values.copyInto(
                values, destinationOffset = destinationIndex + 4,
                startIndex = destinationIndex, endIndex = currentIndex + 1
            )
            values[normalizeIndex(destinationIndex + 1)] = p1
            values[normalizeIndex(destinationIndex + 2)] = p2
            values[normalizeIndex(destinationIndex + 3)] = p3
        }
    } else if (destinationIndex == lastIndex) {
        copyStart()
        values[0] = p1
        values[1] = p2
        values[2] = p3
    } else if (destinationIndex == lastIndex - 1) {
        val l1 = values[lastIndex]
        copyStart()
        values[0] = p2
        values[1] = p3
        values[3] = l1
        values[lastIndex] = p1
    } else if (destinationIndex == lastIndex - 2) {
        val l1 = values[lastIndex - 1]
        val l2 = values[lastIndex]
        copyStart()
        values[0] = p3
        values[1] = l1
        values[3] = l2
        values[lastIndex - 1] = p1
        values[lastIndex] = p2
    } else {
        val l1 = values[lastIndex - 2]
        val l2 = values[lastIndex - 1]
        val l3 = values[lastIndex]
        values.copyInto(
            values,
            destinationOffset = destinationIndex + 4,
            startIndex = destinationIndex,
            endIndex = values.size - 3
        )
        copyStart()
        values[0] = l1
        values[1] = l2
        values[2] = l3
        values[destinationIndex + 1] = p1
        values[destinationIndex + 2] = p2
        values[destinationIndex + 3] = p3
    }
    move++
    currentIndex = newCurrent
}

fun log(message: Any? = "", debug: Boolean = DEBUG) {
    if (debug) println(message)
}

inline fun log(debug: Boolean = DEBUG, message: () -> Any?) {
    if (debug) {
        println(message())
    }
}