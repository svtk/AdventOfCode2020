package day13

import util.readDayInput
import util.readSampleInput

fun main() {
    val lines = readDayInput("day13")
    val arrivalTime = lines.first().toInt()
    val buses = lines[1].split(",").mapNotNull { it.toIntOrNull() }
    println(arrivalTime)
    println(buses)
    val lowestWaitingTime = { time: Int -> time - arrivalTime % time }
    val busId = buses.minByOrNull(lowestWaitingTime)!!
    val waitingTime = buses.minOf(lowestWaitingTime)
    println(busId)
    println(waitingTime)
    println(busId * waitingTime)
}