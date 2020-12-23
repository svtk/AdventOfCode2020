package util

import java.io.File

fun readDayInput(day: String, file: String = "input") =
    File("src/main/kotlin/$day/$file.txt").readLines()
fun readSampleInput(day: String, file: String = "sampleInput") =
    File("src/main/kotlin/$day/$file.txt").readLines()

fun List<String>.splitByEmptyLines(): List<List<String>> =
    fold(mutableListOf<MutableList<String>>()) { list, line ->
        if (list.isEmpty() || line.isBlank()) {
            list += mutableListOf<String>()
        }
        if (line.isNotBlank()) {
            list.last() += line
        }
        list
    }

val debug = false
fun log(message: Any?) {
    if (debug) println(message)
}