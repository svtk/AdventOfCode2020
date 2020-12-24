package day22.part2

import day22.part2.GameResult.A_WINS
import day22.part2.GameResult.B_WINS
import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines

val N = 50

fun main() {
    val input = readSampleInput("day22").splitByEmptyLines()
    val (playerA, playerB) =
//      readSampleInput("day22", "sampleInputPart2")
        input.map { list ->
            Player(list.subList(1, list.size).map(String::toInt))
        }
    val game = playGame(playerA, playerB, GameIndex.nextIndex())
    val finalConfiguration = game.finalConfiguration
    log("\n\n== Post-game results ==")
    finalConfiguration?.logDecks()
    println(finalConfiguration?.calculateScore())
}

enum class GameResult(val winner: String) { A_WINS("1"), B_WINS("2") }
data class Result(val gameResult: GameResult, val finalConfiguration: RoundConfiguration?)

object PlayedGames {
    private val games = mutableMapOf<RoundConfiguration, Result>()

    fun checkGame(roundConfiguration: RoundConfiguration): Result? =
        games[roundConfiguration]

    fun recordGame(roundConfiguration: RoundConfiguration, result: Result) {
        games[roundConfiguration] = result
    }
}

fun playGame(playerA: Player, playerB: Player, gameIndex: Int): Result {
    log("=== Game $gameIndex ===")
    val configuration = RoundConfiguration(playerA, playerB)
    log(configuration)
    val oldResult = PlayedGames.checkGame(configuration)
    if (oldResult != null) return oldResult.also { println("Repetition!") }
    val round = Round(configuration, setOf(), 1, gameIndex)
    val rounds = generateSequence(round) { it.nextRound() }.toList()
    val resultingRound = rounds.last()
    val gameResult = when {
        resultingRound.repeatsItself() -> A_WINS
        resultingRound.playerA.size > resultingRound.playerA.size -> A_WINS
        else -> B_WINS
    }
    log("The winner of game $gameIndex is player ${gameResult.winner}!")
    return Result(gameResult, resultingRound.configuration)
        .also { PlayedGames.recordGame(configuration, Result(gameResult, null)) }
}

fun IntArray.tail() = IntArray(N) { this.getOrElse(it + 1) { 0 } }

data class Player(
    val cards: IntArray,
    val size: Int,
) {
    constructor(list: List<Int>): this(IntArray(N), list.size) {
        for ((index, element) in list.withIndex()) {
            cards[index] = element
        }
    }
    fun isEmpty() = size == 0
    fun nextRound(number: Int): Player {
        val new = IntArray(N)
        cards.copyInto(new, startIndex = 1, endIndex = number + 1)
        return Player(new, number)
    }
    fun losing() = Player(cards.tail(), size - 1)
    fun winning(first: Int, second: Int): Player {
        val tail = cards.tail()
        tail[size - 1] = first
        tail[size] = second
        return Player(tail, size + 1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (!cards.contentEquals(other.cards)) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cards.contentHashCode()
        result = 31 * result + size
        return result
    }
}

data class Round(
    val configuration: RoundConfiguration,
    val prevConfigurations: Set<RoundConfiguration>,
    val roundIndex: Int,
    val gameIndex: Int,
) {
    val playerA get() = configuration.playerA
    val playerB get() = configuration.playerB
    fun repeatsItself() = configuration in prevConfigurations
}

data class RoundConfiguration(
    val playerA: Player,
    val playerB: Player,
)

fun RoundConfiguration.calculateScore(): Long {
    val winner = listOf(playerA, playerB).maxByOrNull { it.size }!!
    val size = winner.size
    return winner.cards
        .foldIndexed(0L) { index, acc, element -> acc + (size - index) * element }
}

fun Round.nextRound(): Round? {
    if (repeatsItself()) return null
    if (playerA.isEmpty() || playerB.isEmpty()) return null
    val cardA = playerA.cards.first()
    val cardB = playerB.cards.first()

    logRoundStart(cardA, cardB)

    fun Player.hasEnoughCards() = size - 1 >= cards.first()
    if (!playerA.hasEnoughCards() || !playerB.hasEnoughCards()) {
        return if (cardA >= cardB) {
            createNextRound(playerA.winning(cardA, cardB), playerB.losing(), A_WINS)
        } else {
            createNextRound(playerA.losing(), playerB.winning(cardB, cardA), B_WINS)
        }
    }

    log("Playing a sub-game to determine the winner...\n")
    val game = playGame(
        playerA.nextRound(cardA),
        playerB.nextRound(cardB),
        GameIndex.nextIndex()
    )
    log("\n...anyway, back to game $gameIndex.")
    return if (game.gameResult == A_WINS) {
        createNextRound(playerA.winning(cardA, cardB), playerB.losing(), A_WINS)
    } else {
        createNextRound(playerA.losing(), playerB.winning(cardB, cardA), B_WINS)
    }
}

fun Round.createNextRound(playerA: Player, playerB: Player, roundResult: GameResult): Round {
    val newConfiguration = RoundConfiguration(playerA, playerB)
    logRoundResult(roundResult)
    return Round(newConfiguration, prevConfigurations + configuration, roundIndex + 1, gameIndex)
}

fun Round.logRoundStart(cardA: Int, cardB: Int) {
    log()
    log("-- Round $roundIndex (Game $gameIndex) --")
    configuration.logDecks()
    log("Player 1 plays: $cardA")
    log("Player 2 plays: $cardB")
}

fun RoundConfiguration.logDecks() {
    fun Player.logCards() = cards.filter { it != 0 }.joinToString()
    log("Player 1's deck: ${playerA.logCards()} size = ${playerA.size}")
    log("Player 2's deck: ${playerB.logCards()} size = ${playerB.size}")
}

fun Round.logRoundResult(gameResult: GameResult) {
    log("Player ${gameResult.winner} wins round $roundIndex of game $gameIndex!")
}

object GameIndex {
    private var maxIndex = 1
    fun nextIndex(): Int = maxIndex++
}

val debug = false
fun log(message: Any? = "") {
    if (debug) println(message)
}