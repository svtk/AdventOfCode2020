package day20

import util.log
import util.readDayInput
import util.splitByEmptyLines


fun main() {
    val tiles = readDayInput("day20").splitByEmptyLines().map { it.toTile() }
//    tiles.forEach(::log)
    val tileConnections = buildTileConnections(tiles)
//    log(tileConnections)
    val corners = findCornerTiles(tiles, tileConnections)
    val tileIdMap = tiles.associateBy { it.id }
    val bigSquareSize = kotlin.math.sqrt(tiles.size.toDouble()).toInt()
    val bigSquare = buildBuildSquare(bigSquareSize, tileIdMap, corners, tileConnections)
    log(displayResult(bigSquare))
}

fun buildBuildSquare(
    size: Int,
    tileIdMap: Map<Int, TileInfo>,
    corners: List<Int>,
    tileConnections: TileConnections
): BigSquare {
    val bigSquareBuilder = BigSquareBuilder(size)
    for (i in 0 until size) {
        for (j in 0 until size) {
            val positionedTile = if (i == 0 && j == 0) {
                val firstTile = tileIdMap.getValue(corners.first())
                positionFirstTile(firstTile, tileConnections)
                    .also { log("Found first tile ${i}x$j $it") }
                    .also { log(it.content); log("\n") }
            } else if (i == 0) {
                val left = bigSquareBuilder[i, j - 1]!!
                log("Looking for edge tile ${i}x$j left=${left.name}")
                findAndPositionUpperEdgeTile(left, tileConnections)
                    .also { log("Found edge tile ${i}x$j: $it") }
                    .also { log(it?.content); log("\n") }
            } else if (j == 0) {
                val upper = bigSquareBuilder[i - 1, j]!!
                log("Looking for edge tile ${i}x$j upper=${upper.name}")
                findAndPositionLeftEdgeTile(upper, tileConnections)
                    .also { log("Found edge tile ${i}x$j: $it") }
                    .also { log(it?.content); log("\n") }
            } else {
                val left = bigSquareBuilder[i, j - 1]!!
                val upper = bigSquareBuilder[i - 1, j]!!
                log("Looking for tile ${i}x$j left=${left.name}, upper=${upper.name}")
                findAndPositionNewTile(left, upper, tileConnections)
                    .also { log("Found neighboring tile ${i}x$j: $it") }
                    .also { log(it?.content); log("\n") }
            }
            if (positionedTile != null)
                bigSquareBuilder[i, j] = positionedTile
        }
    }
    return bigSquareBuilder.toBigSquare()
}

class BigSquareBuilder(val size: Int) {
    val tiles = MutableList(size) { MutableList<PositionedTile?>(size) { null } }
    operator fun get(i: Int, j: Int): PositionedTile? = tiles.getOrNull(i)?.getOrNull(j)
    operator fun set(i: Int, j: Int, PositionedTile: PositionedTile) {
        tiles[i][j] = PositionedTile
    }
    @Suppress("UNCHECKED_CAST")
    fun toBigSquare() = BigSquare(size, tiles as List<List<PositionedTile>>)
}

fun displayResult(
    bigSquare: BigSquare
) = buildString {
    for (j in 0 until bigSquare.size) {
        for (i in 0 until bigSquare.size) {
            append(" " + bigSquare.tiles[i][j].name)
        }
        appendLine()
    }
}

fun positionFirstTile(tileInfo: TileInfo, tileConnections: TileConnections): PositionedTile {
    val initial = tileInfo.initial
    val northIndex = (0..3).first {
        tileConnections.isEdge(initial.getShifted(it)) &&
                tileConnections.isEdge(initial.getShifted(it - 1))
    }
    return PositionedTile(initial, rotation = 4 - northIndex)
}

fun findAndPositionUpperEdgeTile(
    left: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val tile = findNeighbor(left.tile, left.eastSide, tileConnections)!!
    return positionRightTile(tile, left, tileConnections)
}

fun findAndPositionLeftEdgeTile(
    upper: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val tile = findNeighbor(upper.tile, upper.southSide, tileConnections)!!
    return positionDownTile(tile, upper, tileConnections)
}

fun findNeighbor(
    tile: Tile?,
    side: Int?,
    tileConnections: TileConnections,
): Tile? {
    if (tile == null || side == null) return null
    return tileConnections.getCorrespondingTile(tile, side)
        .also { log("Looking for neighbor: $side $tile; $it") }
}


fun findAndPositionNewTile(
    left: PositionedTile,
    upper: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val asDownNeighbor = findNeighbor(upper.tile, upper.southSide, tileConnections)
    val asRightNeighbor = findNeighbor(left.tile, left.eastSide, tileConnections)
    if (asDownNeighbor != null && asRightNeighbor != null && asDownNeighbor != asRightNeighbor) {
        throw IllegalStateException(
            "Two possible neighbors for left=${left.name}: $asRightNeighbor\n" +
                    "and upper=${upper.name}: $asDownNeighbor"
        )
    }
    return if (asDownNeighbor != null) {
        positionDownTile(asDownNeighbor, upper, tileConnections)
    } else if (asRightNeighbor != null) {
        positionRightTile(asRightNeighbor, left, tileConnections)
    } else null
}

fun positionRightTile(tile: Tile, left: PositionedTile, tileConnections: TileConnections): PositionedTile? {
    val westIndex = tile.sides.indexOf(tileConnections.getCorrespondingEdge(left.eastSide))
    if (westIndex == -1) return null
    val northIndex = westIndex + 1
    return PositionedTile(tile, rotation = 4 - northIndex)
}

fun positionDownTile(tile: Tile, upper: PositionedTile, tileConnections: TileConnections): PositionedTile? {
    val northIndex = tile.sides.indexOf(tileConnections.getCorrespondingEdge(upper.southSide))
    if (northIndex == -1) return null
    return PositionedTile(tile, rotation = 4 - northIndex)
}