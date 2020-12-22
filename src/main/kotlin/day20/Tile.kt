package day20

class Tile(
    val index: Char,
    val tileInfo: TileInfo,
    val content: TileContent
) {
    val name: String
        get() = "${tileInfo.name}-$index"

    val sides: List<Int>
        get() = content.sides

    fun getShifted(index: Int) = sides[(4 + index) % 4]

    val flipped: Tile
        get() = tileInfo.tiles.first { it != this }

    override fun toString(): String {
        return "Oriented($name, $sides)"
    }
}

//val OrientedTile.contentWithoutBorders: List<String> get() =
//    content
//        .subList(1, content.lastIndex)
//        .map { it.substring(1, it.lastIndex) }


class TileInfo(
    val id: Int,
    content: TileContent,
    charName: String
) {
    val initial: Tile
        get() = tiles[0]

    val name: String = "$charName-$id"

    val tiles: List<Tile> = listOf(
        Tile('n', this, content),
        Tile('f', this, content.flip()),
    )

    val allSides: List<Int>
        get() = tiles[0].sides + tiles[1].sides

    override fun toString(): String {
        return "Tile($name, sides=[${tiles[0].sides.joinToString(",")}; " +
                "${(tiles[1].sides).joinToString(",")}])"
    }
}

data class PositionedTile(
    val tile: Tile,
    val rotation: Int,
) {
    val northSide: Int
        get() = tile.getShifted(- rotation)
    val eastSide: Int
        get() = tile.getShifted(1 - rotation)
    val southSide: Int
        get() = tile.getShifted(2 - rotation)
    val westSide: Int
        get() = tile.getShifted(3 - rotation)
    val name: String
        get() = "Positioned(${tile.name})"

    val content: TileContent
        get() = tile.content.rotateRight(rotation)

    override fun toString(): String {
        return "PositionedTile(tile=${tile.name}, " +
                "north=$northSide, east=$eastSide, south=$southSide, west=$westSide; " +
                "rotation=$rotation)"
    }
}
