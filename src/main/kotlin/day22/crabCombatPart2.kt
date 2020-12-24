package day22.part2

import day22.part2.Winner.A_WINS
import day22.part2.Winner.B_WINS
import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines

val DEBUG = false

fun main() {
    val input = readDayInput("day22").splitByEmptyLines()
    val (playerA, playerB) =
//      readSampleInput("day22", "sampleInputPart2")
        input.map { list ->
            PlayerOptimized(list.subList(1, list.size).map(String::toInt))
        }
    val game = playGame(RoundConfiguration(playerA, playerB), GameIndex.nextIndex())
    val finalConfiguration = game.finalConfiguration
    log("\n\n== Post-game results ==")
    finalConfiguration.logDecks(true)
    println(finalConfiguration.calculateScore())
}

enum class Winner(private val s: String) {
    A_WINS("1"), B_WINS("2");

    override fun toString() = s
}

data class GameResult(
    val winner: Winner,
    val finalConfiguration: RoundConfiguration,
)

data class ResultWithConfigurations(
    val gameResult: GameResult,
    val prevConfigurations: Set<RoundConfiguration>,
) {
    constructor(
        winner: Winner,
        finalConfiguration: RoundConfiguration,
        prevConfigurations: Set<RoundConfiguration>,
    ): this(GameResult(winner, finalConfiguration), prevConfigurations)
}

object PlayedGames {
    private val games = mutableMapOf<RoundConfiguration, GameResult>()

    fun checkGame(roundConfiguration: RoundConfiguration): GameResult? =
        games[roundConfiguration]

    fun recordGame(roundConfiguration: RoundConfiguration, gameResult: GameResult) {
        games[roundConfiguration] = gameResult
    }
}

fun playGame(configuration: RoundConfiguration, gameIndex: Int): GameResult {
    log("=== Game $gameIndex ===")
    log(configuration)
    val oldResult = PlayedGames.checkGame(configuration)
    if (oldResult != null) return oldResult//.also { println("Repetition!") }
    val round = Round(configuration, setOf(), 1, gameIndex)
    val resultWithConfigurations = playNextRound(round)
    log("The winner of game $gameIndex is player $resultWithConfigurations!")
    resultWithConfigurations.prevConfigurations.forEach { prevConf ->
        PlayedGames.recordGame(prevConf, resultWithConfigurations.gameResult)
    }
    return resultWithConfigurations.gameResult
}

fun List<Int>.tail() = subList(1, size)

interface Player {
    val size: Int
    val first: Int
    operator fun get(i: Int): Int
    fun losing(): Player
    fun winning(card1: Int, card2: Int): Player
    fun isEmpty(): Boolean
    fun hasEnoughCards() = size - 1 >= first
    fun nextGamePlayer(size: Int, replacements: Map<Int, Int>): Player
    fun cards(): List<Int>
}

data class PlayerCompact(val cards: List<Int>) : Player {
    override val first get() = cards.first()
    override val size get() = cards.size
    override fun get(i: Int): Int = cards[i]

    override fun losing() = PlayerCompact(cards.tail())
    override fun winning(card1: Int, card2: Int) = PlayerCompact(cards.tail() + card1 + card2)
    override fun isEmpty() = cards.isEmpty()

    override fun nextGamePlayer(
        size: Int,
        replacements: Map<Int, Int>
    ) = PlayerCompact(
        cards
            .subList(1, 1 + size)
            .map { replacements.getValue(it) }
    )

    override fun cards(): List<Int> = cards
}

data class PlayerOptimized(
    val cardsArray: IntArray
) : Player {
    constructor(list: List<Int>) : this(list.toIntArray())

    override val size
        get() = cardsArray.size

    override val first: Int
        get() = cardsArray.first()

    override fun get(i: Int): Int = cardsArray[i]

    override fun isEmpty() = size == 0

    private fun IntArray.tail(size: Int) = IntArray(size) { this.getOrElse(it + 1) { 0 } }
    override fun losing() = PlayerOptimized(cardsArray.tail(size - 1))

    override fun winning(card1: Int, card2: Int): PlayerOptimized {
        val tail = cardsArray.tail(size + 1)
        tail[size - 1] = card1
        tail[size] = card2
        return PlayerOptimized(tail)
    }

    override fun cards(): List<Int> = cardsArray.toList()

    override fun nextGamePlayer(
        size: Int,
        replacements: Map<Int, Int>
    ): PlayerOptimized {
        val new = IntArray(size)
        cardsArray.copyInto(new, startIndex = 1, endIndex = size + 1)
        for (i in 0 until size) {
            new[i] = replacements.getValue(new[i])
        }
        return PlayerOptimized(new)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerOptimized

        if (!cardsArray.contentEquals(other.cardsArray)) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cardsArray.contentHashCode()
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
) {
    fun nextGameConfiguration(): RoundConfiguration {
        val cardA = playerA.first
        val cardB = playerB.first
        val values = mutableListOf<Int>()
        for (i in 1..cardA) {
            values += playerA[i]
        }
        for (j in 1..cardB) {
            values += playerB[j]
        }
        values.sort()
        val replacements =
            values.withIndex().associate { it.value to it.index + 1 }

        val nextA = playerA.nextGamePlayer(cardA, replacements)
        val nextB = playerB.nextGamePlayer(cardB, replacements)
        return RoundConfiguration(nextA, nextB)
    }
}

fun RoundConfiguration.calculateScore(): Long {
    val winner = listOf(playerA, playerB).maxByOrNull { it.size }!!
    val size = winner.size
    return winner.cards()
        .foldIndexed(0L) { index, acc, element -> acc + (size - index) * element }
}

tailrec fun playNextRound(round: Round): ResultWithConfigurations {
    if (round.repeatsItself()) {
        return ResultWithConfigurations(A_WINS, round.configuration, round.prevConfigurations)
    }
    if (round.playerA.isEmpty() || round.playerB.isEmpty()) {
        val winner = if (round.playerA.isEmpty()) B_WINS else A_WINS
        return ResultWithConfigurations(winner, round.configuration, round.prevConfigurations)
    }

    val oldResult = PlayedGames.checkGame(round.configuration)
    if (oldResult != null) return ResultWithConfigurations(oldResult, emptySet())

    val cardA = round.playerA.first
    val cardB = round.playerB.first

    round.logRoundStart(cardA, cardB)

    val nextRound = if (!round.playerA.hasEnoughCards() || !round.playerB.hasEnoughCards()) {
        if (cardA >= cardB) {
            round.createNextRound(round.playerA.winning(cardA, cardB), round.playerB.losing(), A_WINS)
        } else {
            round.createNextRound(round.playerA.losing(), round.playerB.winning(cardB, cardA), B_WINS)
        }
    } else {
        log("Playing a sub-game to determine the winner...\n")
        val game = playGame(
            round.configuration.nextGameConfiguration(),
            GameIndex.nextIndex()
        )
        log("\n...anyway, back to game ${round.gameIndex}.")
        if (game.winner == A_WINS) {
            round.createNextRound(round.playerA.winning(cardA, cardB), round.playerB.losing(), A_WINS)
        } else {
            round.createNextRound(round.playerA.losing(), round.playerB.winning(cardB, cardA), B_WINS)
        }
    }
    return playNextRound(nextRound)
}

fun Round.createNextRound(playerA: Player, playerB: Player, roundResult: Winner): Round {
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

fun RoundConfiguration.logDecks(debug: Boolean = DEBUG) {
    fun Player.logCards() = cards().filter { it != 0 }.joinToString()
    log("Player 1's deck: ${playerA.logCards()}", debug)
    log("Player 2's deck: ${playerB.logCards()}", debug)
}

fun Round.logRoundResult(winner: Winner) {
    log("Player $winner wins round $roundIndex of game $gameIndex!")
}

object GameIndex {
    private var maxIndex = 1
    fun nextIndex(): Int = maxIndex++
}

fun log(message: Any? = "", debug: Boolean = DEBUG) {
    if (debug) println(message)
}