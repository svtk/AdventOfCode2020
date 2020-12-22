//package day19.matching1
//
//import util.readSampleInput
//import util.splitByEmptyLines
//
//fun main() {
//    val (rulesInput, messagesInput) = readSampleInput("day19").splitByEmptyLines()
//    val rules = rulesInput.map(String::toRule)
//
//    val rulesMap = rules.associateBy { it.index }
//    rules.filterIsInstance<RefRuleBuilder>().forEach { it.build(rulesMap) }
//    rules.forEach { println(it) }
//
//
////    val backRefs = countBackRefs(rules)
////    println(backRefs)
//}
//
//class RuleSet(
//    val rules: MutableMap<Int, Rule>,
//    val backRefs: Map<Int, Set<Int>>,
//) {
//    fun simplify() {
//        val simplifiedRules = rules.values.filter { it.isSimplified }
//        simplifiedRules.forEach {
//
//        }
//    }
//}
//
//fun countBackRefs(rules: List<Rule>): MutableMap<Int, MutableSet<Int>> {
//    val backRefs = mutableMapOf<Int, MutableSet<Int>>()
//    rules.filterIsInstance<RefRuleBuilder>().forEach { rule ->
//        (rule.left + rule.right).forEach { ref ->
//            val set = backRefs.getOrPut(ref.index) { mutableSetOf() }
//            set += rule.index
//        }
//    }
//    return backRefs
//}
//
//interface Rule {
//    val index: Int
//    val isSimplified: Boolean
//}
//
//interface RefRule : Rule {
//    val left: List<Rule>
//    val right: List<Rule>
//    override val isSimplified: Boolean
//        get() = (left + right).all { it is SimpleRule }
//}
//
//interface SimpleRule : Rule {
//    val options: Set<String>
//    override val isSimplified: Boolean
//        get() = true
//}
//
//class RefRuleBuilder(
//    override val index: Int,
//    private val leftIndices: List<Int>,
//    private val rightIndices: List<Int>,
//) : RefRule {
//    override val left: List<Rule>
//        get() = leftBuilder
//    override val right: List<Rule>
//        get() = rightBuilder
//
//    private lateinit var leftBuilder : List<Rule>
//    private lateinit var rightBuilder : List<Rule>
//
//    fun build(map: Map<Int, Rule>) {
//        leftBuilder = leftIndices.map { map.getValue(it) }
//        rightBuilder = rightIndices.map { map.getValue(it) }
//    }
//
//    override fun toString(): String {
//        return "RefRuleBuilder(index=$index, left=$left, right=$right)"
//    }
//}
//
//class SimpleRuleImpl(
//    override val index: Int,
//    override val options: Set<String>
//) : SimpleRule {
//    override fun toString(): String {
//        return "SimplifiedRuleImpl(index=$index, options=$options)"
//    }
//}
//
//fun simplifyRule(refRule: RefRule): SimpleRule {
////    refRule.left.joinToString {  }
//}
//
//fun String.toRule(): Rule {
//    println(this)
//    val index = substringBefore(":").toInt()
//    val quote = "\""
//    if (contains(quote)) {
//        return SimpleRuleImpl(
//            index,
//            setOf(substringAfter(quote).substringBefore(quote))
//        )
//    }
//    val left = substringAfter(": ")
//        .substringBefore(" | ").split(" ").map(String::toInt)
//    val right = if (contains(" | ")) {
//        substringAfter(" | ").split(" ").map(String::toInt)
//    } else {
//        listOf()
//    }
//    return RefRuleBuilder(index, left, right)
//}