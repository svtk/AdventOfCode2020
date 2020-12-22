package day3

import util.readDayInput

fun main() {
    val lines = readDayInput("day3")
    val width = lines.first().length
    val moves = listOf(
        Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(1, 2)
    )
    for ((right, down) in moves) {
        println("right: $right, down: $down, trees: ${countTrees(lines, width, right, down)}")
    }
    val result = moves.fold(1L) { acc, (right, down) ->
        acc * countTrees(lines, width, right, down)
    }

    println(result)

/*
Right 1, down 1.
Right 3, down 1. (This is the slope you already checked.)
Right 5, down 1.
Right 7, down 1.
Right 1, down 2.
 */
//    val trees = countTrees(lines, width, right = 3)
//    println(trees)
}

private fun countTrees(
    lines: List<String>,
    width: Int,
    right: Int,
    down: Int = 1,
): Int {
    return (1 until lines.size).count {
        it * down in lines.indices &&
                lines[it * down][it * right % width] == '#'
    }
}