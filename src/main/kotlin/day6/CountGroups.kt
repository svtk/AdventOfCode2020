package day6

import util.readDayInput
import util.splitByEmptyLines

fun main() {
    val groups = readDayInput("day6").splitByEmptyLines()
    val sumOfUnions = groups
        .map { it.joinToString("").toList().toSet().size }
        .sum()

    println(sumOfUnions)

    val sumOfIntersections = groups
//        .map { it.fold(setOf<Char>()) { acc, s -> acc.intersect(s.toSet()) }.size }
//        .onEach { println(it) }
        .map { list -> list.map(String::toSet) }
//        .onEach { println(it) }
        .map { list ->
            list.reduceOrNull { acc, s -> acc.intersect(s.toSet()) }?.size ?: 0
        }
//        .onEach { println(it) }
        .sum()
    println(sumOfIntersections)
}