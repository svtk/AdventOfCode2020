package day20

class BigSquare(val size: Int, val tiles: List<List<PositionedTile>>) {
    fun getTile(i: Int, j: Int): PositionedTile = tiles[i][j]

    val tileSize = tiles[0][0].content.lines.size

    operator fun get(a: Int, b: Int): Char {
        val iTile = a / tileSize
        val iInside = a % tileSize

        val jTile = b / tileSize
        val jInside = b % tileSize

        return getTile(iTile, jTile).content.lines[iInside][jInside]
    }
}
