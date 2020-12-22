package day20

import util.readSampleInput
import util.splitByEmptyLines

fun main() {
    val tiles = readSampleInput("day20").splitByEmptyLines().map { it.toTile() }
    tiles.forEach(::println)

    println(convertTileSideToInt(".##...####"))
    println(convertTileSideToInt("...##....."))
    println(convertTileSideToInt(".#..#....."))
    println(convertTileSideToInt("###....##."))
}
