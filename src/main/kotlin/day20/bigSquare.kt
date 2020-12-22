package day20

import util.log
import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines
import kotlin.IllegalStateException


fun main() {
    val tiles = readSampleInput("day20").splitByEmptyLines().map { it.toTile() }
//    tiles.forEach(::println)
    val tileConnections = buildTileConnections(tiles)
//    println(tileConnections)
    val corners = findCornerTiles(tiles, tileConnections)
    val tileIdMap = tiles.associateBy { it.id }
    val bigSquareSize = kotlin.math.sqrt(tiles.size.toDouble()).toInt()
    val bigSquare = buildBuildSquare(bigSquareSize, tileIdMap, corners, tileConnections)
    println(displayResult(bigSquare))
}

fun buildBuildSquare(
    size: Int,
    tileIdMap: Map<Int, TileInfoImpl>,
    corners: List<Int>,
    tileConnections: TileConnections
): BigSquare {
    val bigSquare = BigSquare(size)
    for (i in 0 until size) {
        for (j in 0 until size) {
            val positionedTile = if (i == 0 && j == 0) {
                val firstTile = tileIdMap.getValue(corners.first())
                positionFirstTile(firstTile, tileConnections)
                    .also { println("Found first tile ${i}x$j $it\n") }
                    .also { println(it.tile.content) }
            } else if (i == 0) {
                val left = bigSquare[i, j - 1]!!
                println("Looking for edge tile ${i}x$j left=${left.name}")
                findAndPositionUpperEdgeTile(left, tileConnections)
                    .also { println("Found edge tile ${i}x$j: $it\n") }
                    .also { println((it as PositionedTile).tile.content) }
            } else if (j == 0) {
                val upper = bigSquare[i - 1, j]!!
                println("Looking for edge tile ${i}x$j upper=${upper.name}")
                findAndPositionLeftEdgeTile(upper, tileConnections)
                    .also { println("Found edge tile ${i}x$j: $it\n") }
            } else {
                val left = bigSquare[i, j - 1]!!
                val upper = bigSquare[i - 1, j]!!
                println("Looking for tile ${i}x$j left=${left.name}, upper=${upper.name}")
                findAndPositionNewTile(left, upper, tileConnections)
                    .also { println("Found neighboring tile ${i}x$j: $it\n") }
                    .also { println((it as PositionedTile).tile.content) }
            }
            if (positionedTile != null)
                bigSquare[i, j] = positionedTile
        }
    }
    return bigSquare
}

class BigSquare(val size: Int) {
    val tiles = MutableList(size) { MutableList<PositionedTile?>(size) { null } }
    operator fun get(i: Int, j: Int): PositionedTile? = tiles.getOrNull(i)?.getOrNull(j)
    operator fun set(i: Int, j: Int, PositionedTile: PositionedTile) {
        tiles[i][j] = PositionedTile
    }
}

fun displayResult(
    bigSquare: BigSquare
) = buildString {
    for (j in 0 until bigSquare.size) {
        for (i in 0 until bigSquare.size) {
            append(" " + bigSquare.tiles[i][j]?.name)
        }
        appendLine()
    }
}

fun throwNoTile(i: Int, j: Int, bigSquare: BigSquare): Nothing {
    println(displayResult(bigSquare))
    throw IllegalStateException("No corresponding tile for ($i, $j)")
}

fun positionFirstTile(tile: TileInfo, tileConnections: TileConnections): PositionedTile {
    val orientedTile = tile.initial
    val (edgeSideA, edgeSideB) = orientedTile.sides.withIndex().filter { (_, side) -> tileConnections.isEdge(side) }
    val (west, north, east, south) =
        if (edgeSideA.index == 0 && edgeSideB.index == 3)
            listOf(
                edgeSideB.value, edgeSideA.value,
                orientedTile.sides[1], orientedTile.sides[2]
            )
        else
            listOf(
                edgeSideA.value, edgeSideB.value,
                orientedTile.getShifted(edgeSideB.index + 1), orientedTile.getShifted(edgeSideB.index + 2)
            )
    return PositionedTile(orientedTile, west, north, east, south)
}

fun findAndPositionUpperEdgeTile(
    left: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val tileInfo = findNeighbor(left.tile, left.eastSide, tileConnections)!!
    println("Found neighbor $tileInfo")
    return tileInfo.tiles
        .mapNotNull { positionRightTile(it, left) }
        .firstOrNull()
        .also { println("PositionedTile: $it") }
}

fun findAndPositionLeftEdgeTile(
    upper: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val tileInfo = findNeighbor(upper.tile, upper.southSide, tileConnections)!!
    return tileInfo.tiles
        .mapNotNull { positionDownTile(it, upper) }
        .firstOrNull()
}

fun findNeighbor(
    tile: Tile?,
    side: Int?,
    tileConnections: TileConnections,
): TileInfo? {
    if (tile == null || side == null) return null
    return tileConnections.data.getValue(side)
        .firstOrNull { it != tile.tileInfo }
}


fun findAndPositionNewTile(
    left: PositionedTile,
    upper: PositionedTile,
    tileConnections: TileConnections
): PositionedTile? {
    val asDownNeighbor = findNeighbor(upper.tile, upper.southSide, tileConnections)
    val asRightNeighbor = findNeighbor(left.tile, left.eastSide, tileConnections)
    if (asDownNeighbor != null && asRightNeighbor != null && asDownNeighbor != asRightNeighbor) {
        throw IllegalStateException("Two possible neighbors for left=${left.name}: $asRightNeighbor\n" +
                "and upper=${upper.name}: $asDownNeighbor")
    }
    return if (asDownNeighbor != null) {
        positionDownTile(asDownNeighbor, upper)
    } else if (asRightNeighbor != null) {
        positionRightTile(asRightNeighbor, left)
    } else null
}

fun positionRightTile(tile: TileInfo, left: PositionedTile): PositionedTile? =
    tile.tiles.mapNotNull { positionRightTile(it, left) }.firstOrNull()
        .also { log("Positioned right to ${left.name}: $it") }

fun positionDownTile(tile: TileInfo, upper: PositionedTile): PositionedTile? =
    tile.tiles.mapNotNull { positionDownTile(it, upper) }.firstOrNull()
        .also { log("Positioned down to ${upper.name}: $it") }

fun positionRightTile(tile: Tile, left: PositionedTile): PositionedTile? {
    val westIndex = tile.sides.indexOf(left.eastSide)
    if (westIndex == -1) return null
    val (west, north, east, south) = tile.getShiftedSides(from = westIndex)
    return PositionedTile(tile, west, north, east, south)
}

fun positionDownTile(tile: Tile, upper: PositionedTile): PositionedTile? {
    val northIndex = tile.sides.indexOf(upper.southSide)
    if (northIndex == -1) return null
    val (north, east, south, west) = tile.getShiftedSides(from = northIndex)
    return PositionedTile(tile, west, north, east, south)
}