package day20

fun main() {
    val content = TileContent(test.lines())
    display("Initial:", content)
//    println(convertTileSideToInt("#.##.#...."))
//    println(convertTileSideToInt("....#.##.#"))
//    display("Rotated 4 times:", content.rotateRight().rotateRight().rotateRight().rotateRight())
    display("Rotated:", content.rotateRight())
    display("Rotated twice:", content.rotateRight().rotateRight())
//    display("Rotated 3 times:", content.rotateRight().rotateRight().rotateRight())
    display("Flipped:", content.flip())
    display("Flipped & rotated:", content.flip().rotateRight())
}

private fun display(title: String, tileContent: TileContent) {
    println(title)
    println(tileContent.sides)
    println(tileContent)
}

class TileContent(private val lines: List<String>) {
    val stringSides = listOf(
        lines.first(),
        lines.map { it.last() }.joinToString(""),
        lines.last().reversed(),
        lines.map { it.first() }.joinToString("").reversed(),
    )

    val sides: List<Int>
        get() {
            return stringSides.map { convertTileSideToInt(it) }
        }

    fun flip(): TileContent =
        TileContent(lines.map { it.reversed() })

    fun rotateRight(i: Int): TileContent {
        val number = (4 + i) % 4
        return (1..number).fold(this) { content, _ -> content.rotateRight() }
    }

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
    #.##...##.
    #.####...#
    .....#..##
    #...######
    .##.#....#
    .###.#####
    ###.##.##.
    .###....#.
    ..#.#..#.#
    #...##.#..
""".trimIndent()