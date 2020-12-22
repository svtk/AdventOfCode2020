package day1

import util.readDayInput

fun main() {
    val numbers = readDayInput("day1").map { it.toInt() }
    for (first in numbers) {
        for (second in numbers) {
            for (third in numbers) {
                if (first + second + third == 2020) {
                    println(first.toLong() * second * third)
                }
            }
        }
    }
}