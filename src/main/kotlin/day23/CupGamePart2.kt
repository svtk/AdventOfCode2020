package day23.part2

import util.readDayInput
import util.readSampleInput

class Node(
    val value: Int,
) {
    lateinit var next: Node
    override fun toString(): String {
        return "Node(value=$value, next=${next.value})"
    }

}

val SIZE = 1000000
val MOVES = 10000000
val DEBUG = false

class NodesData(
    private val nodes: Map<Int, Node>
) {
    operator fun get(i: Int) = nodes.getValue(i)
    override fun toString() = nodes.toString()
}

fun main() {
    val input = readDayInput("day23").first().map { it - '0' }
    val max = input.maxOrNull()!!
    val initialList = (input + ((max + 1)..SIZE).toList())

    val nodesData = NodesData(initialList.associateWith { Node(it) })
    initialList.forEachIndexed { index, value ->
        nodesData[value].next = nodesData[initialList[(index + 1) % SIZE]]
    }
    val cups = Cups(1, nodesData, nodesData[initialList[0]])
    repeat(MOVES) {
        cups.nextMove()
    }
    println(cups.resultPart2Numbers())
    println(cups.resultPart2())
}

fun Cups.resultPart2(): Long {
    val one = nodesData[1]
    return 1L * one.next.value * one.next.next.value
}

fun Cups.resultPart2Numbers(): Pair<Int, Int> {
    val one = nodesData[1]
    return Pair(one.next.value, one.next.next.value)
}


class Cups(
    var move: Int,
    val nodesData: NodesData,
    var current: Node
)

fun fromNodeSequence(from: Node) =
    generateSequence(from) { node -> node.next.takeIf { it != from } }

fun minusOneSequence(nodesData: NodesData, from: Node) =
    generateSequence(from) { node ->
        if (node.value == 1)
            nodesData[SIZE]
        else
            nodesData[node.value - 1]
    }

fun Cups.nextMove() {
    log { "-- move $move --" }
    log {
        "cups: " + fromNodeSequence(current).withIndex().joinToString("") { (index, node) ->
            val value = node.value
            if (index == 0) "($value)" else " $value "
        }
    }
    val pickedUp = fromNodeSequence(current.next).take(3).toList()
    log { "pick up: " + pickedUp.joinToString { "${it.value}" } }
    val destination = minusOneSequence(nodesData, current).first { it !in pickedUp && it != current }

    val x = pickedUp[2].next
    val y = destination.next

    current.next = x
    destination.next = pickedUp[0]
    pickedUp[2].next = y

    log { "destination: ${destination.value}" }
    log { "sequence: " + fromNodeSequence(current).toList() }
    log()
    move++
    current = current.next
}

fun log(message: Any? = "", debug: Boolean = DEBUG) {
    if (debug) println(message)
}

inline fun log(debug: Boolean = DEBUG, message: () -> Any?) {
    if (debug) {
        println(message())
    }
}
