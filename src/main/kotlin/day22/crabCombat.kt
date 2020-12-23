package day22

import util.readDayInput
import util.splitByEmptyLines

fun main() {
    val (playerA, playerB) = readDayInput("day22")
        .splitByEmptyLines()
        .map { Player(it.subList(1, it.size).map(String::toInt)) }
    val round = Round(playerA, playerB)
    val rounds = generateSequence(round) { it.nextRound() }
    val resultingRound = rounds.last()
    println(resultingRound)
    println(resultingRound.calculateScore())
}

data class Player(val cards: List<Int>) {
    private fun List<Int>.trimFirst() = subList(1, size)
    fun losing() = Player(cards.trimFirst())
    fun winning(winningCards: List<Int>) = Player(cards.trimFirst() + winningCards.sortedDescending())
}

data class Round(val playerA: Player, val playerB: Player)

fun Round.winner() = listOf(playerA, playerB).maxByOrNull { it.cards.size }!!

fun Round.calculateScore(): Long {
    val cards = winner().cards
    val size = cards.size
    return cards
        .foldIndexed(0L) { index, acc, element -> acc + (size - index) * element }
}


fun Round.nextRound(): Round? {
    if (playerA.cards.isEmpty() || playerB.cards.isEmpty()) return null
    val cardA = playerA.cards.first()
    val cardB = playerB.cards.first()
    val winningCards = listOf(cardA, cardB)
    return if (cardA < cardB) {
        Round(playerA.losing(), playerB.winning(winningCards))
    } else {
        Round(playerA.winning(winningCards), playerB.losing())
    }
}