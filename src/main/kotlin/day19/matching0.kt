//package day19
//
//import util.readSampleInput
//import util.splitByEmptyLines
//
//fun main() {
//    val (rulesInput, messagesInput) = readSampleInput("day19").splitByEmptyLines()
//    val rules = rulesInput.map(String::toRule)
//    rules.forEach { println(it) }
//
//    val rulesMap = rules.associateWith { it.index to it }
//
//}
//
//class RuleBuilder(
//    override val index: Int,
//    val left: List<Int>,
//    val right: List<Int>?,
//): Rule(index) {
//    val leftRules: MutableList<Rule> = mutableListOf()
//    val rightRules: MutableList<Rule> = mutableListOf()
//}
//
//sealed class Rule(open val index: Int)
//data class RefRule(
//    override val index: Int,
//    val left: List<Rule>,
//    val right: List<Rule>?,
//): Rule(index)
//data class LetterRule(
//    override val index: Int,
//    val char: Char,
//): Rule(index)
//
//fun Rule.startSatisfy(string: String): Pair<Boolean, String?> {
//    when (this) {
//        is LetterRule -> {
//            return if (string.startsWith(char))
//                Pair(true, string.substring(1))
//            else
//                Pair(false, null)
//        }
//        is RefRule -> {
//            return Pair(false, null)
//        }
//    }
//}
//
//fun String.toRule(): Rule {
//    println(this)
//    val index = substringBefore(":").toInt()
//    val quote = "\""
//    if (contains(quote)) {
//        return LetterRule(index,
//            substringAfter(quote).substringBefore(quote).single())
//    }
//    val left = substringAfter(": ")
//        .substringBefore(" | ").split(" ").map(String::toInt)
//    val right = if (contains(" | ")) {
//        substringAfter(" | ").split(" ").map(String::toInt)
//    }
//    else {
//        null
//    }
//    return RuleBuilder(index, left, right)
//}