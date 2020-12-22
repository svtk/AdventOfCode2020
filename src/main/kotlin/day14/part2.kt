package day14

import util.readDayInput

fun main() {
    val actions = readDayInput("day14").map(::readAction)
    val resultingMemory = actions.fold(Memory(Mask(""), emptyMap())) { memory: Memory, change: Change ->
        memory.applyChangePart2(change)
    }
    println(resultingMemory)
    println(resultingMemory.data.values.sum())
}

fun Memory.applyChangePart2(
    change: Change
): Memory =
    when (change) {
        is MaskChange -> Memory(change.newMask, data)
        is MemoryChange -> {
            val addressSet = applyMemoryAddressChange(change.address, currentMask)
            Memory(currentMask, data + addressSet.map { it to change.value })
        }
    }

fun applyMemoryAddressChange(address: Long, mask: Mask): List<Long> {
    return applyMaskToAddress(address.toString(radix = 2), mask).map { it.toLong(radix = 2) }
        .also { println("New addresses: $it") }
}

fun applyMaskToAddress(address: String, mask: Mask): List<String> {
    val array = address.toCharArray()
    val updatedMask = buildString {
        for ((index, maskBit) in mask.value.withIndex()) {
            val charIndex = index - mask.size + array.size
            val valueBit = array.getOrNull(charIndex)
            val ch = when (maskBit) {
                '0' -> valueBit ?: '0'
                '1' -> '1'
                'X' -> 'X'
                else -> throw java.lang.IllegalStateException(
                    "Incorrect char in mask: $mask"
                )
            }
            append(ch)
        }
    }
    val xIndices = updatedMask.withIndex().filter { it.value == 'X' }.map { it.index }
    val replacementList = xIndices.fold(listOf<Map<Int, Int>>(mapOf())) {
        listOfLists, xIndex ->
        listOfLists.flatMap { map -> listOf(map + (xIndex to 0), map + (xIndex to 1)) }
    }
    return replacementList.map { replacements ->
        buildString {
            for ((index, bit) in updatedMask.withIndex()) {
                append(if (index in replacements) replacements.getValue(index) else bit)
            }
        }
    }
}