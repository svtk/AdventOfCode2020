package day20

import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines

val seaMonster = ImageContent(
    """
    |                  #
    |#    ##    ##    ###
    | #  #  #  #  #  #
    """.trimMargin().lines()
)

class ImageContent(val lines: List<String>) {
    val size = lines.size
    operator fun get(i: Int, j: Int) = lines.getOrNull(i)?.getOrNull(j)

    fun flip(): ImageContent = ImageContent(flip(lines))

    fun rotateRight(i: Int) = ImageContent(rotateRight(lines, i))

    fun rotateRight() = ImageContent(rotateRight(lines))

    fun countGrid() = lines.map { lines -> lines.count { ch -> ch == '#' } }.sum()

}

fun ImageContent.checkSeaMonsterStart(a: Int, b: Int): Boolean {
    for (i in seaMonster.lines.indices) {
        for (j in seaMonster.lines[1].indices) {
            val imageCh = this[a + i, b + j]
                ?: return false
            val monsterCh = seaMonster[i, j]
            if (monsterCh == '#' && imageCh != '#') {
                return false
            }
        }
    }
    return true
}

fun ImageContent.countSeaMonsters(): Int {
    val size = lines.size
    var counter = 0
    for (i in 0 until size) {
        for (j in 0 until size) {
            if (checkSeaMonsterStart(i, j)) {
                counter++
            }
        }
    }
    return counter
}


fun main() {
    val tiles = readDayInput("day20").splitByEmptyLines().map { it.toTile() }
    val tileConnections = buildTileConnections(tiles)
    val corners = findCornerTiles(tiles, tileConnections)
    val tileIdMap = tiles.associateBy { it.id }
    val size = kotlin.math.sqrt(tiles.size.toDouble()).toInt()
    val bigSquare = buildBuildSquare(size, tileIdMap, corners, tileConnections)
//    val full = bigSquare.fullSquare()
//    println(full.joinToString("\n"))
//    println()
//    println()
//    println()
    val result = bigSquare.trimEdges()
//    println(result.joinToString("\n"))
    val imageContent = ImageContent(result)
//    displayMonsterResult(imageContent)
//    displayMonsterResult(imageContent.rotateRight())
//    displayMonsterResult(imageContent.rotateRight(2))
//    displayMonsterResult(imageContent.rotateRight(3))
//    val flipped = imageContent.flip()
//    displayMonsterResult(flipped)
//    displayMonsterResult(flipped.rotateRight())
//    displayMonsterResult(flipped.rotateRight(2))
//    displayMonsterResult(flipped.rotateRight(3))

    println(imageContent.countGrid() - 34 * seaMonster.countGrid())
}

fun displayMonsterResult(imageContent: ImageContent) {
    println(imageContent.countSeaMonsters())
}