package day25

import util.readDayInput
import util.readSampleInput

val BIG_PRIME = 20201227
val SUBJECT_NUMBER = 7L

fun transform(loopSize: Int, subject: Long): Long {
    var res = 1L
    repeat(loopSize) {
        res *= subject
        res %= BIG_PRIME
    }
    return res
}

fun main() {
    val (cardPublicKey, doorPublicKey) = readDayInput("day25").map { it.toLong() }
    val cardLoopSize = 13207740 // 8
    println(cardLoopSize)
    println(transform(cardLoopSize, SUBJECT_NUMBER) == cardPublicKey)

    val doorLoopSize = 8229037 // 11
    println(doorLoopSize)
    println(transform(doorLoopSize, SUBJECT_NUMBER) == doorPublicKey)

    println(transform(cardLoopSize, subject = doorPublicKey))
    println(transform(doorLoopSize, subject = cardPublicKey))
}