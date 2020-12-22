package day4

import util.readDayInput

enum class Entry(
    val value: String,
    val validator: (String) -> Boolean,
    val mandatory: Boolean = true
) {
    BirthYear("byr", yearValidator(1920..2002)),
    IssueYear("iyr", yearValidator(2010..2020)),
    ExpirationYear("eyr", yearValidator(2020..2030)),
    Height("hgt", heightValidator),
    HairColor("hcl", hairColorValidator),
    EyeColor("ecl", eyeColorValidator),
    PassportID("pid", passNumberValidator),
    CountryID("cid", { true }, mandatory = false),
}

fun yearValidator(period: IntRange) =
    fun(s: String): Boolean {
        val year = s.toIntOrNull() ?: return false
        return year in period
    }

val heightValidator =
    fun(s: String): Boolean {
        fun validateHeight(measure: String, bounds: IntRange): Boolean {
            if (s.endsWith(measure)) {
                val height = s.removeSuffix(measure).toIntOrNull() ?: return false
                return height in bounds
            }
            return false
        }
        return (validateHeight("cm", 150..193)) ||
                validateHeight("in", 59..76)
    }

val hairColorValidator =
    fun(s: String): Boolean {
        if (s.length != 7) return false
        if (s.first() != '#') return false
        return s.removePrefix("#").all { it in '0'..'9' || it in 'a'..'f' }
    }

val passNumberValidator =
    { s: String -> (s.length == 9) &&  s.all { it in '0'..'9' } }

val eyeColorValidator =
    { s: String -> s in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth") }

fun createEntry(from: String, context: String) =
    Entry.values().find { it.value == from }
        ?: throw IllegalArgumentException("Wrong entry '$from' in '$context'")

data class PassDetails(val details: Map<Entry, String?>)

fun PassDetails.isValid() =
    Entry.values().filter { it.mandatory }.all { it in details }

fun main() {
    val passports = readDayInput("day4")
        .fold(mutableListOf<MutableList<String>>()) { list, line ->
            if (list.isEmpty() || line.isBlank()) {
                list += mutableListOf<String>()
            }
            list.last() += line
            list
        }
        .map { strings ->
            val details = strings
                .joinToString(" ") // converting newline to whitespace
                .split(" ")
                .filter { it.isNotEmpty() }
                .mapNotNull {
                    val entry = createEntry(it.substringBefore(":"), it)
                    val value = it.substringAfter(":")
                    (entry to value)
                        .takeIf { entry.validator(value) }
                }
                .toMap()
            PassDetails(details)
        }
    println(passports.count { it.isValid() })
}