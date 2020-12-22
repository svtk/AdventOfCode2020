package day20

import util.readDayInput
import util.splitByEmptyLines

val seaMonster = """
    |                  #
    |#    ##    ##    ###
    | #  #  #  #  #  #
    """.trimMargin()


fun main() {
    val tiles = readDayInput("day20").splitByEmptyLines().map { it.toTile() }
    val tileConnections = buildTileConnections(tiles)
    val corners = findCornerTiles(tiles, tileConnections)
    val tileIdMap = tiles.associateBy { it.id }
    val size = kotlin.math.sqrt(tiles.size.toDouble()).toInt()
    val bigSquare = buildBuildSquare(size, tileIdMap, corners, tileConnections)

    for (i in 0 until size) {
        for (j in 0 until size) {
            val positionedTile = bigSquare[i, j] as PositionedTile
//            println(positionedTile.orientedTile.contentWithoutBorders.size)
//            println(positionedTile.orientedTile.contentWithoutBorders.first().length)
//            println(positionedTile.orientedTile.contentWithoutBorders)
//            println(positionedTile.orientedTile.content)
        }
    }
}