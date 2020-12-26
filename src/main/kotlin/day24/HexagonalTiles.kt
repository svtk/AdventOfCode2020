package day24

import day24.Color.*
import util.readDayInput
import util.readSampleInput

data class Tile(val x: Int, val y: Int) {
    fun adjacent() = listOf(east(), west(), southeast(), southwest(), northeast(), northwest())
    fun east() = Tile(x + 2, y)
    fun west() = Tile(x - 2, y)
    fun southeast() = Tile(x + 1, y - 1)
    fun northeast() = Tile(x + 1, y + 1)
    fun southwest() = Tile(x - 1, y - 1)
    fun northwest() = Tile(x - 1, y + 1)
}

enum class Color {
    BLACK, WHITE;

    fun flip() = when (this) {
        BLACK -> WHITE
        WHITE -> BLACK
    }
}

class HexagonalTiles(val data: Map<Tile, Color> = mapOf()) {
    operator fun get(tile: Tile): Color = data[tile] ?: WHITE
}

fun HexagonalTiles.flip(tile: Tile): HexagonalTiles {
    val current = data[tile] ?: WHITE
    return HexagonalTiles(data + (tile to current.flip()))
}

fun HexagonalTiles.addEdges(): HexagonalTiles {
    val allEdges = data.keys.flatMap { it.adjacent() }
    val newEdges = allEdges - data.keys
    return HexagonalTiles(data + newEdges.associateWith { WHITE })
}

fun HexagonalTiles.countBlackAdjacentTiles(tile: Tile) =
    tile.adjacent().count { this[it] == BLACK }

fun HexagonalTiles.nextDay(): HexagonalTiles {
    // Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
    val toWhite = data.filter { (tile, color) ->
        color == BLACK && countBlackAdjacentTiles(tile).let { size -> size == 0 || size > 2 }
    }.keys
    // Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
    val toBlack = data.filter { (tile, color) ->
        color == WHITE && countBlackAdjacentTiles(tile) == 2
    }.keys
    return HexagonalTiles(data + toWhite.associateWith { WHITE } + toBlack.associateWith { BLACK })
        .addEdges()
}

fun HexagonalTiles.countBlackTiles() = data.count { (_, color) -> color == BLACK }

fun main() {
    val tiles = readDayInput("day24").map(::readTile)
    val hexagonalTiles =
        tiles.fold(HexagonalTiles()) { hexagonalTiles, tileInfo -> hexagonalTiles.flip(tileInfo.tile) }
            .addEdges()
    printTiles(tiles)
    println(hexagonalTiles.countBlackTiles())
//    printBlackTiles(hexagonalTiles)
    println("----")

    val result = (1..100).fold(hexagonalTiles) { dayTiles, day ->
        dayTiles.nextDay().also {
            println("Day $day: ${it.countBlackTiles()}")
//            printBlackTiles(it)
        }
    }
    println("Result: ${result.countBlackTiles()}")
}

private fun printBlackTiles(hexagonalTiles: HexagonalTiles) {
    hexagonalTiles.data
        .filter { (_, color) -> color == BLACK }.keys
        .sortedWith(compareBy({ it.y }, { it.x }))
        .forEach(::println)
}

fun printTiles(tiles: List<TileInfo>) {
    tiles
        .sortedWith(compareBy({ it.tile.y }, { it.tile.x }))
        .forEach { println("tile: ${it.tile} path: ${it.path.simplifyPath()}") }
}

private fun countRepetitiveTiles(tiles: List<TileInfo>) {
    val repetitiveTiles = tiles
        .groupBy { it.tile }
        .mapValues { (_, group) -> group.size }
        .filter { (_, groupSize) -> groupSize > 1 }
    println("Repetitive tiles: $repetitiveTiles")
}

data class TileInfo(val path: List<String>, val tile: Tile)

fun List<String>.simplifyPath(): List<String> {
    val pairs = listOf(
        "e" to "w",
        "se" to "nw",
        "ne" to "sw",
    )
    val firstSimplification = mutableListOf<String>()
    for ((first, second) in pairs) {
        val firstCount = count { it == first }
        val secondCount = count { it == second }
        val min = minOf(firstCount, secondCount)
        repeat(firstCount - min) { firstSimplification.add(first) }
        repeat(secondCount - min) { firstSimplification.add(second) }
    }
    val result = mutableListOf<String>()
    val triples = listOf(Triple("ne", "w", "se"), Triple("sw", "e", "nw"))
    for ((a, b, c) in triples) {
        val aCount = count { it == a }
        val bCount = count { it == b }
        val cCount = count { it == c }
        val min = minOf(aCount, bCount, cCount)
        repeat(aCount - min) { result.add(a) }
        repeat(bCount - min) { result.add(b) }
        repeat(cCount - min) { result.add(c) }
    }
    return result
}

fun readTile(s: String): TileInfo {
    var tile = Tile(0, 0)
    var current = 0
    val path = mutableListOf<String>()
    while (current <= s.lastIndex) {
        val twoCharsCommand = "${s[current]}${s.getOrNull(current + 1)}"
        val oneCharCommand = "${s[current]}"
        tile = when (twoCharsCommand) {
            "se" -> tile.southeast().also { path += twoCharsCommand; current += 2 }
            "sw" -> tile.southwest().also { path += twoCharsCommand; current += 2 }
            "ne" -> tile.northeast().also { path += twoCharsCommand; current += 2 }
            "nw" -> tile.northwest().also { path += twoCharsCommand; current += 2 }
            else -> {
                when (oneCharCommand) {
                    "e" -> tile.east().also { path += oneCharCommand; current++ }
                    "w" -> tile.west().also { path += oneCharCommand; current++ }
                    else -> throw IllegalStateException("Wrong input: $s")
                }
            }
        }
    }
    return TileInfo(path, tile)
}