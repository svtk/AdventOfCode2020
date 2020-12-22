//package day20
//
//import org.junit.Assert
//import org.junit.Test
//
//class TestPositionFirstTile {
//    data class TestTile(
//        override val id: Int,
//        override val sides: List<Int>,
//    ) : Tile {
//        override val flipped by lazy {
//            TestTile(id, sides
//                .map { value -> value + 1000 }
//                .let { l -> listOf(l[0], l[3], l[2], l[1]) })
//        }
//    }
//
//    private val testTiles = listOf(
//        TestTile(100, listOf(1, 2, 3, 4)),
//        TestTile(101, listOf(3, 5, 6, 7)),
//        TestTile(102, listOf(8, 4, 9, 19)),
//        TestTile(103, listOf(7, 11, 12, 9)),
//    )
//
//    private fun testFirstTilePositioning(transform: (List<Tile>) -> List<Tile>) {
//        val tiles = transform(testTiles)
//        val testTileConnections = buildTileConnections(tiles)
//        val res = positionFirstTile(tiles.first(), testTileConnections)
//        Assert.assertEquals(1, res.westSide)
//        Assert.assertEquals(2, res.northSide)
//        Assert.assertEquals(3, res.eastSide)
//        Assert.assertEquals(4, res.southSide)
//    }
//
//    @Test
//    fun test1() {
//        testFirstTilePositioning { it }
//    }
//
//    private fun List<Int>.shift(value: Int) = listOf(
//        this[value % 4],
//        this[(1 + value) % 4],
//        this[(2 + value) % 4],
//        this[(3 + value) % 4]
//    )
//
//    @Test
//    fun test2() {
//        testFirstTilePositioning { list ->
//            val firstTile = list.first()
//            listOf(
//                TestTile(firstTile.id, firstTile.sides.shift(1)),
//                list[3], list[1], list[2]
//            )
//        }
//    }
//
//    @Test
//    fun test3() {
//        testFirstTilePositioning { list ->
//            val firstTile = list.first()
//            listOf(
//                TestTile(firstTile.id, firstTile.sides.shift(2)),
//                list[2], list[1], list[3]
//            )
//        }
//    }
//
//    @Test
//    fun test4() {
//        testFirstTilePositioning { list ->
//            val firstTile = list.first()
//            listOf(
//                TestTile(firstTile.id, firstTile.sides.shift(3)),
//                list[1], list[3], list[2]
//            )
//        }
//    }
//}