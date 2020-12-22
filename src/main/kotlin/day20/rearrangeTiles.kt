package day20

import util.readSampleInput
import util.splitByEmptyLines

fun main() {
    val tiles = readSampleInput("day20").splitByEmptyLines().map { it.toTile() }
    println(tiles.size)
    tiles.forEach { println("$it ${it.tiles}") }
    val tileConnections = buildTileConnections(tiles)
    println(tileConnections)
    val corners = findCornerTiles(tiles, tileConnections)
    println(corners)
    // part 1 answer
    println(corners.fold(1L) { mult, i -> mult * i })
}

fun findCornerTiles(
    tiles: List<TileInfo>,
    tileConnections: TileConnections
) = tiles
    .filter { tileConnections.isCorner(it) }
    .map { it.id }

fun convertTileSideToInt(tileSide: String) =
    tileSide.replace(".", "0").replace("#", "1").toInt(radix = 2)

class TileConnections(val data: Map<Int, Set<TileInfo>>) {
    fun isCorner(tile: TileInfo) = tile.initial.sides.count(::isEdge) == 2
    fun isEdge(side: Int) = data[side]?.size == 1
    override fun toString(): String = buildString {
        data.forEach { (sideValue, tileSet) ->
            appendLine("($sideValue): ${tileSet.map { "${it.id}" }}")
        }
    }
}

fun buildTileConnections(tiles: List<TileInfo>): TileConnections {
    val result = mutableMapOf<Int, MutableSet<TileInfo>>()
    tiles.forEach { tile ->
        tile.allSides.forEach {
            val set = result.getOrPut(it) { mutableSetOf() }
            set += tile
        }
    }
    return TileConnections(result)
}

interface TileInfo {
    val id: Int
    val charName: String
    val allSides: Set<Int>
    val initial: Tile
        get() = tiles[0]
    val tiles: List<Tile>
    val name: String
        get() = "$charName-$id"
}

class Tile(
    val index: Char,
    val sides: List<Int>,
    val tileInfo: TileInfo,
    val content: List<String>
) {
    val name: String
        get() = tileInfo.name

    fun getContent(): String = content.joinToString("\n") + "\n"

    fun getShifted(index: Int) = sides[index % 4]

    fun getShiftedSides(from: Int) = listOf(
        getShifted(from),
        getShifted(from + 1),
        getShifted(from + 2),
        getShifted(from + 3),
    )

    override fun toString(): String {
        return "Oriented($name-$index, $sides)"
    }
}

//val OrientedTile.contentWithoutBorders: List<String> get() =
//    content
//        .subList(1, content.lastIndex)
//        .map { it.substring(1, it.lastIndex) }


class TileInfoImpl(
    override val id: Int,
    val lines: List<String>,
    override val charName: String
) : TileInfo {
    private val stringSides = listOf(
        lines.first(),
        lines.map { it.last() }.joinToString(""),
        lines.last().reversed(),
        lines.map { it.first() }.joinToString("").reversed(),
    )
    private val initialSides: List<Int> =
        stringSides.map { convertTileSideToInt(it) }

    override lateinit var tiles: List<Tile>

    fun buildOrientedTiles() {
        val flippedSides: List<Int> =
            stringSides.map { convertTileSideToInt(it.reversed()) }.reversed()

        tiles = listOf(
            Tile('n', initialSides, this, lines),
            Tile('f', flippedSides, this, lines.map { it.reversed() }.reversed()),
        )
    }

    override val allSides: Set<Int> by lazy {
        tiles.flatMap { it.sides }.toSet()
    }

    override fun toString(): String {
        return "Tile($name, sides=[${initialSides.joinToString(",")}; " +
                "${(tiles[1].sides).joinToString(",")}])"
    }
}

private val nameGenerator = NameGenerator()
fun List<String>.toTile() =
    TileInfoImpl(
        first().substringAfter("Tile ").substringBefore(":").toInt(),
        subList(1, size),
        nameGenerator.nextName(),
    ).also { it.buildOrientedTiles() }