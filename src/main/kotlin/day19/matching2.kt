package day19.matching2

import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines

fun main() {
    val (rulesInput, messagesInput) = readDayInput("day19", "inputPart2").splitByEmptyLines()
    val rules = rulesInput.map(String::toRule)

    val rulesMap = rules.associateBy { it.index }
    rules.filterIsInstance<RefRuleBuilder>().forEach { it.build(rulesMap) }
//    rules.forEach { println(it) }

    println("============")

    val zeroRule = rulesMap.getValue(0)
//    println(satisfyAtStart(zeroRule, "ababbb", 0))
//    for (message in messagesInput) {
//        println("$message ${satisfy(zeroRule, message)}")
//    }
    val result = messagesInput.count { satisfy(zeroRule, it) }
    println(result)
}

interface Rule {
    val index: Int
    val isSimplified: Boolean
}

interface RefRule : Rule {
    val left: List<Rule>
    val right: List<Rule>
    override val isSimplified: Boolean
        get() = (left + right).all { it is LetterRule }
}

interface LetterRule : Rule {
    val char: Char
    override val isSimplified: Boolean
        get() = true
}

fun satisfy(rule: Rule, string: String): Boolean =
    string.length in satisfyAtStart(rule, string, 0)

fun satisfyAtStart(rule: Rule, initialString: String, from: Int): Set<Int> {
//    println("F($from) $rule")
    if (from !in initialString.indices) return setOf()
    val string = initialString.substring(from)
    if (rule is LetterRule) {
        return if (string.startsWith(rule.char))
            setOf(from + 1)//.also { println("$it $rule") }
        else
            setOf()
    }
    rule as RefRule
    val leftSet = satisfyList(rule.left, initialString, from)
    val rightSet = satisfyList(rule.right, initialString, from)
    return (leftSet + rightSet)//.also { println("$it $rule") }
}

fun satisfyList(rules: List<Rule>, initialString: String, from: Int): Set<Int> {
    return when {
        rules.isEmpty() -> emptySet()
        rules.size == 1 -> satisfyAtStart(rules[0], initialString, from)
        rules.size == 2 -> satisfyAtStart(rules[0], initialString, from).flatMap {
            satisfyAtStart(rules[1], initialString, it)
        }.toSet()
        else -> satisfyAtStart(rules[0], initialString, from).flatMap { from1 ->
            satisfyAtStart(rules[1], initialString, from1).flatMap { from2 ->
                satisfyAtStart(rules[2], initialString, from2)
            }
        }.toSet()
    }
}

class RefRuleBuilder(
    override val index: Int,
    private val leftIndices: List<Int>,
    private val rightIndices: List<Int>,
) : RefRule {
    override val left: List<Rule>
        get() = leftBuilder
    override val right: List<Rule>
        get() = rightBuilder

    private lateinit var leftBuilder: List<Rule>
    private lateinit var rightBuilder: List<Rule>

    fun build(map: Map<Int, Rule>) {
        leftBuilder = leftIndices.map { map.getValue(it) }
        rightBuilder = rightIndices.map { map.getValue(it) }
    }

    override fun toString(): String {
        return "RefRule($index, left=$left, right=$right)"
    }
}

class LetterRuleImpl(
    override val index: Int,
    override val char: Char,
) : LetterRule {
    override fun toString(): String {
        return "LetterRule($index, $char)"
    }
}

fun String.toRule(): Rule {
//    println(this)
    val index = substringBefore(":").toInt()
    val quote = "\""
    if (contains(quote)) {
        return LetterRuleImpl(
            index,
            substringAfter(quote).substringBefore(quote).single()
        )
    }
    val left = substringAfter(": ")
        .substringBefore(" | ").split(" ").map(String::toInt)
    val right = if (contains(" | ")) {
        substringAfter(" | ").split(" ").map(String::toInt)
    } else {
        listOf()
    }
    return RefRuleBuilder(index, left, right)
}

