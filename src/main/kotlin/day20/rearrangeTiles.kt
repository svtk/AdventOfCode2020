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

private val nameGenerator = NameGenerator()
fun List<String>.toTile() =
    TileInfoImpl(
        first().substringAfter("Tile ").substringBefore(":").toInt(),
        TileContent(subList(1, size)),
        nameGenerator.nextName(),
    ).also { it.buildOrientedTiles() }