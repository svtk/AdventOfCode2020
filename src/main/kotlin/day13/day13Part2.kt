package day13part2

import util.readDayInput

data class Constraint(val divider: Long, val offset: Long)

val constraints = readConstraints()

private fun readConstraints(): List<Constraint> =
    readDayInput("day13")[1]
        .split(",")
        .map { it.toLongOrNull() }
        .withIndex()
        .filter { it.value != null }
        .map {
            Constraint(
                it.value!!,
                it.index.toLong()
            )
        }
        .sortedByDescending { it.divider }

fun main() {
    val final = constraints.reduce { a, b ->
        replaceEquation(a, b)
    }
    println(final)
    val answer = final.divider - final.offset
    println(answer)
    println(isAnswer(answer, constraints))
}

fun isAnswer(candidate: Long, constraints: List<Constraint>) =
    constraints.all { (candidate + it.offset) % it.divider == 0L }

fun replaceEquation(a: Constraint, b: Constraint): Constraint {
    // a.divider * n - a.offset = t
    // b.divider * m - b.offset = t
    // (a.divider * n - a.offset + b.offset) % b.divider == 0
    val nMin = (1..b.divider).first { n ->
        (a.divider * n - a.offset + b.offset) % b.divider == 0L
    }

    // a.divider * (nMin + n * b.divider) - a.offset = t
    // (a.divider * b.divider) * n + (a.divider * nMin - a.offset) = t
    //  ^^^^ new divider ^^^^         ^^^^^^^^ new offset ^^^^^^^
    val newDivider = a.divider * b.divider
    val newOffset = -a.divider * nMin + a.offset
    return Constraint(newDivider, if (newOffset < 0L) newOffset + newDivider else newOffset)
}