package day14

inline class Mask(val value: String) {
    val size get() = value.length
    override fun toString() = value
}

sealed class Change
data class MaskChange(val newMask: Mask) : Change()
data class MemoryChange(val address: Long, val value: Long) : Change()

data class Memory(
    val currentMask: Mask,
    val data: Map<Long, Long>
)

fun readAction(line: String): Change {
    val maskPrefix = "mask = "
    val memPrefix = "mem"
    return when {
        line.startsWith(maskPrefix) -> MaskChange(Mask(line.substringAfter(maskPrefix)))
        line.startsWith(memPrefix) -> MemoryChange(
            line.substringAfter("[").substringBefore("]").toLong(),
            line.substringAfter("= ").toLong()
        )
        else -> throw IllegalStateException("Can't parse $line")
    }
}