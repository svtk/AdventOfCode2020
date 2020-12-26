package day25.sandbox

import day25.BIG_PRIME

val CARD_PUBLIC_KEY = 2069194L
val DOOR_PUBLIC_KEY = 16426071L

val CARD_SAMPLE_KEY = 5764801L
val DOOR_SAMPLE_KEY = 17807724L

fun transform(loopSize: Int, subject: Long): Long {
    var res = 1L
    repeat(loopSize) {
        res *= subject
        res %= BIG_PRIME
        if (res == CARD_PUBLIC_KEY) println("Card public key: $it")
        if (res == DOOR_PUBLIC_KEY) println("Door public key: $it")
        if (res == CARD_SAMPLE_KEY) println("Card sample key: $it")
        if (res == DOOR_SAMPLE_KEY) println("Door sample key: $it")
    }
    return res
}


fun main() {
    transform(BIG_PRIME, 7)
}