package day9

import util.readDayInput
import util.readSampleInput

val GROUP_SIZE = 25
val INVALID_NUMBER = 675280050L // 127L

fun main() {
    val numbers = readDayInput("day9").map { it.toLong() }
    findInvalidNumber(numbers)
    for (size in 2..numbers.size) {
        for (fromIndex in numbers.indices) {
            if (fromIndex + size in numbers.indices) {
                val subList = numbers.subList(fromIndex, fromIndex + size)
                if (subList.sum() == INVALID_NUMBER) {
                    println(subList)
                    println(subList.minOrNull()!! + subList.maxOrNull()!!)
                }
            }
        }
    }
}

fun findInvalidNumber(numbers: List<Long>) {
    var currentIndex = GROUP_SIZE
    while (currentIndex in numbers.indices) {
        val currentNumber = numbers[currentIndex]
        val subList = numbers.subList(currentIndex - GROUP_SIZE, currentIndex)
        if (!isSumOfTwoPrevNumbers(currentNumber, subList)) {
            println(currentNumber)
        }
        currentIndex++
    }
}

fun isSumOfTwoPrevNumbers(number: Long, list: List<Long>): Boolean {
    list.withIndex().forEach { (firstIndex, first) ->
        list.withIndex().forEach { (secondIndex, second) ->
            if (firstIndex != secondIndex) {
                if (first + second == number) return true
            }
        }
    }
    return false
}
