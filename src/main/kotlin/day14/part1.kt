package day14

import util.readDayInput

fun main() {
    val actions = readDayInput("day14").map(::readAction)
    val resultingMemory = actions.fold(Memory(Mask(""), emptyMap())) { memory: Memory, change: Change ->
        memory.applyChange(change)
    }
    println(resultingMemory)
    println(resultingMemory.data.values.sum())
}

fun Memory.applyChange(
    change: Change
): Memory =
    when (change) {
        is MaskChange -> Memory(change.newMask, data)
        is MemoryChange -> {
            val newValue = applyMemoryChange(change.value, currentMask)
            Memory(currentMask, data + (change.address to newValue))
        }
    }

fun applyMemoryChange(value: Long, mask: Mask): Long {
    return applyMask(value.toString(radix = 2), mask).toLong(radix = 2)
}

fun applyMask(value: String, mask: Mask): String {
    val array = value.toCharArray()
    return buildString {
        for ((index, maskBit) in mask.value.withIndex()) {
            val charIndex = index - mask.size + array.size
            val valueBit = array.getOrNull(charIndex)
            val ch = when (maskBit) {
                '0' -> '0'
                '1' -> '1'
                'X' -> valueBit ?: '0'
                else -> throw java.lang.IllegalStateException(
                    "Incorrect char in mask: $mask"
                )
            }
            append(ch)
        }
    }
}