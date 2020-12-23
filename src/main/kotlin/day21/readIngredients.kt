package day21

import util.readDayInput
import util.readSampleInput

data class Food(
    val ingredients: List<Ingredient>,
    val allergens: List<Allergen>
)

data class Ingredient(val name: String) {
    override fun toString() = name
}

data class Allergen(val name: String) {
    override fun toString() = name
}

fun main() {
    val foodList = readDayInput("day21").map(String::toFood)
    foodList.forEach(::println)
    val allIngredients = foodList.flatMap { it.ingredients }.toSet()
    val allergens = foodList.flatMap { it.allergens }.toSet()
    val possibleIngredients = allergens.associateWith { allergen ->
        foodList
            .filter { allergen in it.allergens }
            .map { it.ingredients.toSet() }
            .reduce { a, b -> a intersect b }
            .also {
                println("Allergen: $allergen")
                println("Possible ingredients: $it")
                println()
            }
    }
    println("Possible ingredients: $possibleIngredients")
    val ingredientsWithoutAllergens = allIngredients - possibleIngredients.values.flatten()
    println("Ingredients without allergens: $ingredientsWithoutAllergens")
    val sum = foodList.map { food -> food.ingredients.count { it in ingredientsWithoutAllergens } }.sum()
    println(sum)

    val initial = possibleIngredients.map { PossibleIngredients(it.key, it.value) }
    val result = (1..10).fold(initial) { ingredients, _ ->
        ingredients.simplifyPossibleIngredientsList()
    }
    println(result.associate { it.allergen to it.ingredients })
    println(result.sortedBy { it.allergen.name }.map { it.ingredients.single() }.joinToString(","))
}

fun String.toFood() = Food(
    substringBefore(" (contains ").split(" ").map { Ingredient(it) },
    substringAfter(" (contains ").substringBefore(")").split(", ").map { Allergen(it) }
)

data class PossibleIngredients(val allergen: Allergen, val ingredients: Set<Ingredient>) {
    fun isFound() = ingredients.size == 1
}

fun List<PossibleIngredients>.simplifyPossibleIngredientsList(): List<PossibleIngredients> {
    if (all { it.isFound() }) return this
    val foundIngredients = filter { it.isFound() }.flatMap { it.ingredients }
    return map { possible ->
        if (possible.ingredients.size == 1) possible else
        PossibleIngredients(possible.allergen, possible.ingredients - foundIngredients)
    }
}


/*
1 2 3 4 (contains dairy, fish)
5 6 7 1 (contains dairy)
3 6 (contains soy)
3 1 7 (contains fish)

1 = dairy
3 = fish
6 = soy

1 2 3 4 5 6 7
  2   4 5   7


// determine which ingredients can't possibly contain any
of the allergens in any food in your list

Each allergen is found in exactly one ingredient.
Each ingredient contains zero or one allergen.

kfcds, nhms, sbzzf, or trh
2 4 7 5
 */