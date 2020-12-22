package day2

import util.readDayInput

fun main() {
    val lines = readDayInput("day2")
    println(lines.count { line -> getConstraints(line).satisfyNewPolicy() })
}

data class PasswordConstraints(
    val password: String,
    val range: IntRange,
    val char: Char
) {
    fun satisfyPolicy() =
        password.count { it == char } in range

    fun satisfyNewPolicy(): Boolean {
        val first = password[range.first - 1]
        val second = password[range.last - 1]
        return (first == char && second != char) ||
                (first != char && second == char)
    }
}
fun getConstraints(line: String): PasswordConstraints {
    val start = line.substringBefore("-").toInt()
    val end = line.substringAfter("-").substringBefore(" ").toInt()
    val char = line.substringAfter(" ").substringBefore(":").singleOrNull()
        ?: throw IllegalArgumentException("Wrong input: $line")
    val password = line.substringAfter(": ")
    return PasswordConstraints(password, start..end, char)
}