package day23

import util.readSampleInput

private val DEBUG = true

fun main() {
    val initialInput = readSampleInput("day23").first().map { it - '0' }
    val initialCups = Cups(move = 1, initialInput)
    val moves = 100
    val last = generateSequence(initialCups) { it.nextMove() }.take(moves + 1).last()
    println(last.values)
    println(last.result())
}

fun Cups.result(): String {
    val oneIndex = values.indexOf(1)
    return (1..values.lastIndex).joinToString("") { "${this[oneIndex + it]}" }
}

data class Cups(
    val move: Int,
    val values: List<Int>,
) {
    operator fun get(i: Int) = values.getNormalized(i)
}

fun List<Int>.normalizeIndex(i: Int) = i % size
fun List<Int>.getNormalized(i: Int) = this[normalizeIndex(i)]

fun Cups.nextMove(): Cups {
    log("-- move $move --")
    log("cups: " + values.withIndex().joinToString("") { (index, value) ->
        if (index == 0) "($value)" else " $value " }
    )
    val pickedUpIndices = 1..3
    val pickedUp = pickedUpIndices.map { this[it] }
    log("pick up: " + pickedUp.joinToString())
    val currentCup = this[0]
    val circleIndices = 4..values.lastIndex
    val destinationCupIndex = circleIndices
        .filter { this[it] < currentCup }
        .maxByOrNull { this[it] }
        ?: circleIndices.maxByOrNull { this[it] }!!

    val destination = this[destinationCupIndex]
    log("destination: $destination")

    fun subListWithoutPickedUp(start: Int, endExclusive: Int) =
        values
            .subList(start, endExclusive)
            .filter { it !in pickedUp }

    val nextValues = subListWithoutPickedUp(0, destinationCupIndex + 1) +
            pickedUpIndices.map { this[it] } +
            subListWithoutPickedUp(destinationCupIndex + 1, values.size)
    log("next values: $nextValues")
    log()
    return Cups(move + 1, nextValues.indices.map { nextValues.getNormalized(it + 1) })
}

fun log(message: Any? = "", debug: Boolean = DEBUG) {
    if (debug) println(message)
}