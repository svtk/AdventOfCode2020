package tmp

/*
I have a list with numbers, and I have an expression.
I iterate over the list to find what element gives me the maximum value of the expression.
then I return that value, and the index of the element that gave me that maximum value.
I feel like there must be a smarter way to do that as opposed to looping through. Any ideas?
 */
/*
 * Returns the largest value according to the provided [comparator]
 * among all values produced by [selector] function applied to each
 * element in the collection or `null` if there are no elements.
 */

fun expr(i: Int): Int = -i

fun main() {
    val numbers = listOf(1, 3, 2)
//    numbers.maxOfWithOrNull(Comparator { e1, e2 ->  })
    val (index, result) = numbers
        .withIndex()
        .maxByOrNull { (_, num) -> expr(num) }!!
    println("index = $index")
    println("result = $result")
}