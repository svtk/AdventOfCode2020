package day20

fun main() {
    val content = TileContent(test.lines())
    println("Rotated:")
    println(content.rotateRight())
    println("Flipped:")
    println(content.flip())
}

class TileContent(val lines: List<String>) {
    fun flip(): TileContent =
        TileContent(lines.map { it.reversed() }.reversed())

    fun rotateRight(): TileContent {
        val size = lines.size
        check(lines.all { it.length == size })
        val newLines = (0 until size).map { i ->
            buildString {
                (0 until size).map { j ->
                    append(lines[size - 1 - j][i])
                }
            }
        }
        return TileContent(newLines)
    }

    override fun toString() = lines.joinToString("\n")

}

val test = """
    #.##.#....
    ...#......
    #..##.#...
    #...##.#..
    ..#.####.#
    .....#....
    ##..#....#
    #....#....
    ##.###.#..
    ..#.#.#.#.
""".trimIndent()