package day16

import util.readDayInput
import util.readSampleInput
import util.splitByEmptyLines

fun main() {
    val input = readDayInput("day16").splitByEmptyLines()
    val constraints = input.first().map { it.toConstraint() }
    val myTicket = input[1][1].toTicket()
    val nearbyTickets = input[2].run { subList(1, size) }.map { it.toTicket() }
//    println(constraints)
    println(myTicket)
//    println(nearbyTickets)
    // part 1
    val invalidValues = nearbyTickets.flatMap { it.getCompletelyInvalidValues(constraints) }
    println(invalidValues.sum())
//    println(invalidValues)

    // part 2
    val validTickets = nearbyTickets.filter { it.getCompletelyInvalidValues(constraints).isEmpty() }

    val constraintValuesList = buildConstraintValues(constraints, validTickets, myTicket)
//    constraintValuesList.forEach { println(it) }

    val possiblePlacement = PossiblePlacement(constraintValuesList)
    possiblePlacement.initialize(constraints)
    do {
        val constraintsToFix = possiblePlacement.findTheOnlyChoiceConstraints()
        constraintsToFix.forEach { possiblePlacement.fixTheOnlyChoiceConstraint(it) }
    } while (constraintsToFix.isNotEmpty())
    possiblePlacement.map.forEach { (constraintValues, possibleConstraints) ->
        println("${constraintValues.initialIndex} : $possibleConstraints")
    }
    val result = possiblePlacement.toResult()
    val multiplication = result
        .filter { (_, constraint) -> "departure" in constraint.name }
        .keys
        .map { constraintValues ->  myTicket.values[constraintValues.initialIndex] }
        .also { println(it) }
        .fold(1L) { a, b -> a * b }
    println(multiplication)
}


/*
ConstraintValues(values=[7, 7])
ConstraintValues(values=[3, 1])
ConstraintValues(values=[47, 14])

// constraint
class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50
 */
data class ConstraintValues(val initialIndex: Int, val values: List<Int>)

class PossiblePlacement(
    constraintValuesList: List<ConstraintValues>
) {
    val map: Map<ConstraintValues, MutableSet<Constraint>> =
        constraintValuesList.associateWith { mutableSetOf() }

    fun initialize(constraints: List<Constraint>) {
        constraints.forEach { constraint ->
            map.keys.forEach { constraintValues ->
                if (constraintValues.values.all { it in constraint }) {
                    map.getValue(constraintValues) += constraint
                }
            }
        }
    }

    fun findTheOnlyChoiceConstraints(): List<Constraint> {
        return map.values
            .filter { it.size == 1 }
            .flatten()
            .filter { constraintToFix -> map.values.count { constraintToFix in it } > 1 }
    }

    fun fixTheOnlyChoiceConstraint(constraint: Constraint) {
        map.forEach { (_, set) ->
            if (constraint in set && set.size != 1) {
                set -= constraint
            }
        }
    }

    fun toResult() = map.mapValues { it.value.single() }
}

data class Constraint(
    val name: String,
    val first: IntRange,
    val second: IntRange
) {
    operator fun contains(value: Int) =
        value in first || value in second
}

data class Ticket(val values: List<Int>)

fun buildConstraintValues(
    constraints: List<Constraint>,
    validTickets: List<Ticket>,
    myTicket: Ticket
): List<ConstraintValues> {
    val constraintValues = List(constraints.size) { mutableListOf<Int>() }
    (validTickets + myTicket).forEach { ticket ->
        ticket.values.forEachIndexed { index, value ->
            constraintValues[index] += value
        }
    }
    return constraintValues.mapIndexed { index, values -> ConstraintValues(index, values) }
}


fun Ticket.getCompletelyInvalidValues(constraints: List<Constraint>): List<Int> {
    return values.filter { value ->
        constraints.none { constraint ->
            value in constraint
        }
    }
}

fun String.toConstraint(): Constraint {
    val colon = ": "
    val name = substringBefore(colon)
    val or = " or "
    val firstRange = substringAfter(colon).substringBefore(or)
    val secondRange = substringAfter(or)
    fun String.toRange() = IntRange(
        substringBefore("-").toInt(),
        substringAfter("-").toInt()
    )
    return Constraint(name, firstRange.toRange(), secondRange.toRange())
}

fun String.toTicket(): Ticket {
    return Ticket(split(",").map { it.toInt() })
}