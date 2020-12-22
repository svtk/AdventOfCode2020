package day20


class TileConnections(
    private val data: Map<Int, Set<Tile>>,
    private val edgeBindings: Map<Int, Int>,
) {
    fun getCorrespondingEdge(side: Int) = edgeBindings.getValue(side)
    fun getCorrespondingTile(tile: Tile, side: Int): Tile? {
        return data
            .getValue(side)
            .find { it != tile }
            ?.flipped
    }
    fun isCorner(tile: Tile) = tile.sides.count(::isEdge) == 2
    fun isEdge(side: Int) = data[side]?.size == 1
    override fun toString(): String = buildString {
        data.forEach { (side, tiles) ->
            appendLine("($side): ${tiles.map { it.name }}")
        }
    }
}

fun buildTileConnections(tiles: List<TileInfo>): TileConnections {
    val data = mutableMapOf<Int, MutableSet<Tile>>()
    val edgeBindings = mutableMapOf<Int, Int>()
    tiles.forEach { tileInfo ->
        tileInfo.initial.content.stringSides.forEach {
            val side = convertTileSideToInt(it)
            val correspondingSide = convertTileSideToInt(it.reversed())
            edgeBindings[side] = correspondingSide
            edgeBindings[correspondingSide] = side
        }
        tileInfo.tiles.forEach { tile ->
            tile.sides.forEach { side ->
                val set = data.getOrPut(side) { mutableSetOf() }
                set += tile
            }
        }
    }
    return TileConnections(data, edgeBindings)
}
