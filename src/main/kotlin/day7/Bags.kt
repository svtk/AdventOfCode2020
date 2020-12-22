package day7

import util.readDayInput

fun main() {
    val lines = readDayInput("day7")
    val rules = lines.map(String::toRule)
    val initial = Bag("shiny gold")
    val rulesMap = rules.associate { it.bag to it.contentList }
    for ((bag, contentList) in rulesMap) {
        println("Bag: $bag Content: $contentList")
    }
//    countOuterBags(initial, rulesMap)
    countInnerBags(initial, rulesMap)
}

data class Bag(val type: String)
data class Content(val amount: Int, val bag: Bag)
data class Rule(val bag: Bag, val contentList: List<Content>)

fun String.toRule(): Rule {
    val bagType = substringBefore(" bags contain ")
    val content = substringAfter(" bags contain ")

    val contentList =
        if (content == "no other bags.")
            emptyList()
        else {
            content.split(", ").map {
                val amount = it.substringBefore(" ").toInt()
                val type = it.substringAfter(" ").substringBefore(" bag")
                Content(amount, Bag(type))
            }
        }
    return Rule(Bag(bagType), contentList)
}

fun countOuterBags(initial: Bag, rulesMap: Map<Bag, List<Content>>) {
    val outerContainers = mutableMapOf<Bag, MutableSet<Bag>>()
    for ((bag, contentList) in rulesMap) {
        contentList.forEach {
            outerContainers.getOrPut(it.bag) { mutableSetOf() } += bag
        }
    }
    println(dfs(outerContainers, initial).size - 1)
}

fun dfs(graph: Map<Bag, Set<Bag>>, initial: Bag): Set<Bag> {
    val visited = mutableSetOf<Bag>()
    val queue = ArrayDeque<Bag>()
    queue += initial
    while (queue.isNotEmpty()) {
        val bag = queue.removeFirst()
        visited += bag
        graph[bag]?.let { connected ->
            queue += connected
        }
    }
    return visited
}

fun countInnerBags(
    initial: Bag,
    rulesMap: Map<Bag, List<Content>>,
) {
    val result = countInnerBags(initial, rulesMap, mutableMapOf())
    println("Inner bags count: ${result - 1}")
}

private fun countInnerBags(
    initial: Bag,
    rulesMap: Map<Bag, List<Content>>,
    innerBagsCount: MutableMap<Bag, Int>,
): Int {
    if (initial in innerBagsCount) {
        return innerBagsCount.getValue(initial)
    }
    val contentRules = rulesMap[initial]
        ?: throw IllegalArgumentException("No rules for $initial")
    val result = 1 + contentRules
        .sumBy { content ->
            content.amount * countInnerBags(content.bag, rulesMap, innerBagsCount)
        }
    println("Result for $initial is $result")
    innerBagsCount[initial] = result
    return result
}