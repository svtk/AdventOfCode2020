package day20

class NameGenerator {
    private var firstPart = 'A'
    private var secondPart = ' '
    fun nextName(): String {
        if (firstPart == ('Z' + 1)) {
            firstPart = 'A'
            if (secondPart == ' ') {
                secondPart = 'A'
            } else {
                secondPart++
            }
        }
        return "${if (secondPart == ' ') "" else "$secondPart"}${firstPart++}"
    }
}

fun main() {
    val nameGenerator = NameGenerator()
    for (i in 0..144) {
        println(nameGenerator.nextName())
    }
}