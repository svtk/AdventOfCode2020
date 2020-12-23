package day20

class BigSquare(val size: Int, val tiles: List<List<PositionedTile>>) {
    fun getTile(i: Int, j: Int): PositionedTile = tiles[i][j]

    val tileSize = tiles[0][0].content.lines.size

    private fun getChar(a: Int, b: Int): Char {
        val iTile = a / tileSize
        val iInside = a % tileSize

        val jTile = b / tileSize
        val jInside = b % tileSize

        return getTile(iTile, jTile).content.lines[iInside][jInside]
    }

    fun fullSquare(): List<String> {
        val bigSize = size * tileSize
        val array = MutableList(bigSize) { CharArray(bigSize) }
        for (i in 0 until bigSize) {
            for (j in 0 until bigSize) {
                array[i][j] = getChar(i, j)
            }
        }
        return array.map { it.joinToString("") }
    }

    fun isInsideIndex(index: Int): Boolean {
        if (index % tileSize == 0) return false
        if ((index + 1) % tileSize == 0) return false
        return true
    }

    fun markEdges(): List<String> {
        return fullSquare()
            .mapIndexed { index, s ->
                if (isInsideIndex(index)) s else s.replace('.', 'x').replace('#', 'x')
            }
            .map { s ->
                s.mapIndexed { index, ch ->
                    if (isInsideIndex(index)) ch else 'x'
                }.joinToString("")
            }
    }

    fun trimEdges(): List<String> {
        return fullSquare()
            .filterIndexed { index, s ->
                isInsideIndex(index)
            }
            .map { s ->
                s.filterIndexed { index, ch -> isInsideIndex(index) }
            }
    }
}