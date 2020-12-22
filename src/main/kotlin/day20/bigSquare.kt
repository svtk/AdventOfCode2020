package day20

import util.log
import util.readDayInput
import util.splitByEmptyLines
import kotlin.IllegalStateException


fun main() {
    val tiles = readDayInput("day20").splitByEmptyLines().map { it.toTile() }
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
            val positioning = if (i == 0 && j == 0) {
                val firstTile = tileIdMap.getValue(corners.first())
                positionFirstTile(firstTile, tileConnections)
                    .also { println("Found first tile ${i}x$j $it\n") }
                    .also { println((it as PositionedTile).tile.getContent()) }
            } else if (i == 0) {
                val left = bigSquare[i, j - 1] as Positioning
                println("Looking for edge tile ${i}x$j left=${left.name}")
                findAndPositionUpperEdgeTile(left, tileConnections)
                    .also { println("Found edge tile ${i}x$j: $it\n") }
                    .also { println((it as PositionedTile).tile.getContent()) }
            } else if (j == 0) {
                val upper = bigSquare[i - 1, j] as Positioning
                println("Looking for edge tile ${i}x$j upper=${upper.name}")
                findAndPositionLeftEdgeTile(upper, tileConnections)
                    .also { println("Found edge tile ${i}x$j: $it\n") }
            } else {
                val left = bigSquare[i, j - 1] as Positioning
                val upper = bigSquare[i - 1, j] as Positioning
                println("Looking for tile ${i}x$j left=${left.name}, upper=${upper.name}")
                findAndPositionNewTile(left, upper, tileConnections)
                    .also { println("Found neighboring tile ${i}x$j: $it\n") }
                    .also { println((it as PositionedTile).tile.getContent()) }
            }
            bigSquare[i, j] = positioning
        }
    }
    for (i in 1 until size) {
        val right = bigSquare[i, 1] as PositionedTile
        val updatedTile = (bigSquare[i, 0] as UnpositionedTiles).options.first {
            it.eastSide == right.westSide
        }
        bigSquare[i, 0] = updatedTile
    }
    for (j in 1 until size) {
        val down = bigSquare[1, j] as PositionedTile
        val updatedTile = (bigSquare[0, j] as UnpositionedTiles).options.first {
            it.southSide == down.northSide
        }
        bigSquare[0, j] = updatedTile
    }
    return bigSquare
}

class BigSquare(val size: Int) {
    val tiles = MutableList(size) { MutableList<Positioning?>(size) { null } }
    operator fun get(i: Int, j: Int): Positioning? = tiles.getOrNull(i)?.getOrNull(j)
    operator fun set(i: Int, j: Int, positioning: Positioning) {
        tiles[i][j] = positioning
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
    leftPositioning: Positioning,
    tileConnections: TileConnections
): Positioning =
    Positioning(leftPositioning.options.flatMap { left ->
        val tile = findNeighbor(left.tile, left.eastSide, tileConnections)!!
        println("Found neighbor $tile")
        tile.tiles
            .mapNotNull { positionRightTile(it, left) }
            .also { println("After position right tile: $it") }
    })
        .also { if (it.options.isEmpty()) throw IllegalStateException("No edge tile for left=${leftPositioning}") }
        .also { println("Positioning: $it") }

fun findAndPositionLeftEdgeTile(
    upperPositioning: Positioning,
    tileConnections: TileConnections
): Positioning =
    Positioning(upperPositioning.options.flatMap { upper ->
        val tile = findNeighbor(upper.tile, upper.southSide, tileConnections)!!
        tile.tiles
            .mapNotNull { positionDownTile(it, upper) }
//            .filter { tileConnections.isEdge(it.westSide) }
    })
        .also { if (it.options.isEmpty()) throw IllegalStateException("No edge tile for upper=${upperPositioning}") }

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
    left: Positioning,
    upper: Positioning,
    tileConnections: TileConnections
): Positioning {
    val asDownNeighbors = upper.options.associateBy { upperTile ->
        findNeighbor(upperTile.tile, upperTile.southSide, tileConnections)
    }
    val asRightNeighbors =
        left.options.associateBy { leftTile ->
            findNeighbor(leftTile.tile, leftTile.eastSide, tileConnections)
        }
    println("Looking for new tile: ${asDownNeighbors.keys} ${asRightNeighbors.keys}")
    val possibleResultingTiles = (asDownNeighbors.keys intersect asRightNeighbors.keys).filterNotNull()
    if (possibleResultingTiles.isEmpty()) {
        throw IllegalStateException("Can't find neighbor for left=${left.name} and upper=${upper.name}")
    }
    return Positioning(possibleResultingTiles.flatMap { tile ->
        positionRightTile(tile, asRightNeighbors.getValue(tile)).options intersect
                positionDownTile(tile, asDownNeighbors.getValue(tile)).options
    })
}

fun positionRightTile(tile: TileInfo, left: PositionedTile): Positioning =
    Positioning(tile.tiles.mapNotNull { positionRightTile(it, left) })
        .also { log("Positioned right to ${left.name}: $it") }

fun positionDownTile(tile: TileInfo, upper: PositionedTile): Positioning =
    Positioning(tile.tiles.mapNotNull { positionDownTile(it, upper) })
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


sealed class Positioning {
    abstract val name: String
    abstract val options: List<PositionedTile>
}

fun Positioning(options: List<PositionedTile>) =
    if (options.size == 1) options.single()
    else UnpositionedTiles(options.toSet().toList())

data class UnpositionedTiles(
    override val options: List<PositionedTile>
) : Positioning() {
    override val name: String
        get() = "Unpositioned(${options.map { it.tile.name }})"

    override fun toString(): String {
        return "UnpositionedTile(options=" +
                "${options.map { "${it.tile.name}(w=${it.westSide}, n=${it.northSide} e=${it.eastSide} s=${it.southSide})" }})"
    }
}

data class PositionedTile(
    val tile: Tile,
    val westSide: Int,
    val northSide: Int,
    val eastSide: Int,
    val southSide: Int,
) : Positioning() {
    override val name: String
        get() = "Positioned(${tile.name})"
    override val options: List<PositionedTile>
        get() = listOf(this)

    override fun toString(): String {
        return "PositionedTile(tile=${tile.name}, west=$westSide, north=$northSide, east=$eastSide, south=$southSide)"
    }
}