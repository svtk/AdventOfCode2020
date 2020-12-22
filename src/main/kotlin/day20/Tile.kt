package day20

interface TileInfo {
    val id: Int
    val charName: String
    val allSides: Set<Int>
    val initial: Tile
        get() = tiles[0]
    val tiles: List<Tile>
    val content: TileContent
    val name: String
        get() = "$charName-$id"
}

class Tile(
    val index: Char,
    val sides: List<Int>,
    val tileInfo: TileInfo,
    val content: TileContent
) {
    val name: String
        get() = tileInfo.name

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
    override val content: TileContent,
    override val charName: String
) : TileInfo {
    private val stringSides = listOf(
        content.lines.first(),
        content.lines.map { it.last() }.joinToString(""),
        content.lines.last().reversed(),
        content.lines.map { it.first() }.joinToString("").reversed(),
    )
    private val initialSides: List<Int> =
        stringSides.map { convertTileSideToInt(it) }

    override lateinit var tiles: List<Tile>

    fun buildOrientedTiles() {
        val flippedSides: List<Int> =
            stringSides.map { convertTileSideToInt(it.reversed()) }.reversed()

        tiles = listOf(
            Tile('n', initialSides, this, content),
            Tile('f', flippedSides, this, content.flip()),
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

data class PositionedTile(
    val tile: Tile,
    val westSide: Int,
    val northSide: Int,
    val eastSide: Int,
    val southSide: Int,
) {
    val name: String
        get() = "Positioned(${tile.name})"

    override fun toString(): String {
        return "PositionedTile(tile=${tile.name}, west=$westSide, north=$northSide, east=$eastSide, south=$southSide)"
    }
}
