package day20

import util.readSampleInput
import util.splitByEmptyLines

fun main() {
    val tiles = readSampleInput("day20").splitByEmptyLines().map { it.toTile() }
    println(tiles.size)
    tiles.forEach { println("${it.name} ${it.tiles}") }
//    tiles.forEach { tileInfo ->
//        repeat(2) {
//            println("${tileInfo.tiles[it]}")
//            println(tileInfo.tiles[it].content)
//            println()
//        }
//        println("====")
//    }
    val tileConnections = buildTileConnections(tiles)
    println(tileConnections)
//    val corners = findCornerTiles(tiles, tileConnections)
//    println(corners)
    // part 1 answer
//    println(corners.fold(1L) { mult, i -> mult * i })
}

fun findCornerTiles(
    tileInfos: List<TileInfo>,
    tileConnections: TileConnections
) = tileInfos
    .filter { tileConnections.isCorner(it.initial) }
    .map { it.id }

fun convertTileSideToInt(tileSide: String) =
    tileSide.replace(".", "0").replace("#", "1").toInt(radix = 2)

private val nameGenerator = NameGenerator()
fun List<String>.toTile() =
    TileInfo(
        first().substringAfter("Tile ").substringBefore(":").toInt(),
        TileContent(subList(1, size)),
        nameGenerator.nextName(),
    )