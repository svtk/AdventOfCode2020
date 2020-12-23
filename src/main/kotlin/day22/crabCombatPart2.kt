package day22.part2

import day22.part2.GameResult.A_WINS
import day22.part2.GameResult.B_WINS
import util.readDayInput
import util.splitByEmptyLines

fun main() {
    val (playerA, playerB) =
//      readSampleInput("day22", "sampleInputPart2")
        readDayInput("day22")
            .splitByEmptyLines()
            .map { Player(it.subList(1, it.size).map(String::toInt)) }
    val game = playGame(playerA, playerB, GameIndex.nextIndex())
    val finalConfiguration = game.finalConfiguration
    log("\n\n== Post-game results ==")
    finalConfiguration.logDecks()
    println(finalConfiguration.calculateScore())
}

enum class GameResult(val winner: String) { A_WINS("1"), B_WINS("2") }
data class Result(val gameResult: GameResult, val finalConfiguration: RoundConfiguration)

object PlayedGames {
    private val games = mutableMapOf<RoundConfiguration, Result>()

    fun checkGame(roundConfiguration: RoundConfiguration): Result? =
        games[roundConfiguration]

    fun recordGame(roundConfiguration: RoundConfiguration, result: Result) {
        games[roundConfiguration] = result
    }
}

fun playGame(playerA: Player, playerB: Player, gameIndex: Int): Result {
    println("=== Game $gameIndex ===")
    val configuration = RoundConfiguration(playerA, playerB)
    println(configuration)
    val oldResult = PlayedGames.checkGame(configuration)
    if (oldResult != null) return oldResult.also { println("Repetition!") }
    val round = Round(configuration, setOf(), 1, gameIndex)
    val rounds = generateSequence(round) { it.nextRound() }.toList()
    val resultingRound = rounds.last()
    val gameResult = when {
        resultingRound.repeatsItself() -> A_WINS
        resultingRound.playerA.cards.size > resultingRound.playerA.cards.size -> A_WINS
        else -> B_WINS
    }
    println("The winner of game $gameIndex is player ${gameResult.winner}!")
    return Result(gameResult, rounds.last().configuration)
        .also { PlayedGames.recordGame(configuration, it) }
}

fun List<Int>.tail() = subList(1, size)

data class Player(val cards: List<Int>) {
    fun losing() = Player(cards.tail())
    fun winning(winningCards: List<Int>) = Player(cards.tail() + winningCards)
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
    val winner = listOf(playerA, playerB).maxByOrNull { it.cards.size }!!
    val size = winner.cards.size
    return winner.cards
        .foldIndexed(0L) { index, acc, element -> acc + (size - index) * element }
}

fun Round.nextRound(): Round? {
    if (repeatsItself()) return null
    if (playerA.cards.isEmpty() || playerB.cards.isEmpty()) return null
    val cardA = playerA.cards.first()
    val cardB = playerB.cards.first()

    logRoundStart(cardA, cardB)

    fun Player.hasEnoughCards() = cards.size - 1 >= cards.first()
    if (!playerA.hasEnoughCards() || !playerB.hasEnoughCards()) {
        val winningCards = listOf(cardA, cardB).sortedDescending()
        return if (cardA >= cardB) {
            createNextRound(playerA.winning(winningCards), playerB.losing(), A_WINS)
        } else {
            createNextRound(playerA.losing(), playerB.winning(winningCards), B_WINS)
        }
    }

    log("Playing a sub-game to determine the winner...\n")
    val game = playGame(
        Player(playerA.cards.tail().take(cardA)),
        Player(playerB.cards.tail().take(cardB)),
        GameIndex.nextIndex()
    )
    log("\n...anyway, back to game $gameIndex.")
    return if (game.gameResult == A_WINS) {
        createNextRound(playerA.winning(listOf(cardA, cardB)), playerB.losing(), A_WINS)
    } else {
        createNextRound(playerA.losing(), playerB.winning(listOf(cardB, cardA)), B_WINS)
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
    fun Player.logCards() = cards.joinToString()
    log("Player 1's deck: ${playerA.logCards()}")
    log("Player 2's deck: ${playerB.logCards()}")
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