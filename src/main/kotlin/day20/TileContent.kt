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

class TileContent(val lines: List<String>) {
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

    fun flip(): TileContent = TileContent(flip(lines))

    fun rotateRight(i: Int) = TileContent(rotateRight(lines, i))

    fun rotateRight() = TileContent(rotateRight(lines))

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