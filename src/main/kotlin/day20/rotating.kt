package day20

fun rotateRight(lines: List<String>, i: Int): List<String> {
    val number = (4 + i) % 4
    return (1..number).fold(lines) { l, _ -> rotateRight(l) }
}

fun rotateRight(lines: List<String>): List<String> {
    val size = lines.size
    check(lines.all { it.length == size })
    val newLines = (0 until size).map { i ->
        buildString {
            (0 until size).map { j ->
                append(lines[size - 1 - j][i])
            }
        }
    }
    return newLines
}

fun flip(lines: List<String>) = lines.map { it.reversed() }