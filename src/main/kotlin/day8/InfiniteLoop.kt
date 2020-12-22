package day8

import day8.Operation.*
import util.readDayInput

fun main() {
    val lines = readDayInput("day8")
    val instructions = lines.map(String::toInstruction)
//    println(process(instructions))
    val possibleCorruptedIndices = instructions
        .withIndex()
        .filter { (_, instruction) -> instruction.operation != Accumulator }
        .map { it.index }
    for (corruptedIndex in possibleCorruptedIndices) {
        val newInstructions = instructions.fixCorruptedIndex(corruptedIndex)
        val (success, result) = process(newInstructions)
        if (success) println(result)
    }
}

data class Instruction(
    val operation: Operation,
    val argument: Int
)

enum class Operation(val code: String) {
    NoOperation("nop"),
    Accumulator("acc"),
    Jump("jmp")
}

fun String.toInstruction(): Instruction {
    fun wrongInput(): Nothing = throw IllegalArgumentException("Can't parse instruction $this")
    val code = substringBefore(" ")
    val operation = values().find { it.code == code } ?: wrongInput()
    val argument = substringAfter(" ").toIntOrNull() ?: wrongInput()
    return Instruction(operation, argument)
}

fun process(instructions: List<Instruction>): Pair<Boolean, Int> {
    var globalAccumulator = 0
    val processedInstructions = mutableSetOf<Int>()
    tailrec fun processInstruction(index: Int): Boolean {
        if (index in processedInstructions) return false
        if (index == instructions.size) return true
        processedInstructions += index

        val instruction = instructions[index]
        if (instruction.operation == Accumulator) {
            globalAccumulator += instruction.argument
        }
        val newIndex = when (instruction.operation) {
            NoOperation -> index + 1
            Accumulator -> index + 1
            Jump -> index + instruction.argument
        }
        return processInstruction(newIndex)
    }

    val successful = processInstruction(0)
    return Pair(successful, globalAccumulator)
}

fun List<Instruction>.fixCorruptedIndex(corruptedIndex: Int): List<Instruction> {
    val oldInstruction = this[corruptedIndex]
    val newInstruction = when (oldInstruction.operation) {
        NoOperation -> Instruction(Jump, oldInstruction.argument)
        Accumulator -> throw IllegalStateException("Corrupted instruction is Acc: $oldInstruction")
        Jump -> Instruction(NoOperation, oldInstruction.argument)
    }
    return subList(0, corruptedIndex) + newInstruction + subList(corruptedIndex + 1, size)
}