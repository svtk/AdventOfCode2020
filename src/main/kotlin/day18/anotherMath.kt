package day18

import day18.Operation.MULT
import day18.Operation.PLUS
import util.readDayInput
import util.readSampleInput

fun main() {
    val input = readDayInput("day18")
    val sum = input.map {
        line ->
        val tokenList = line.toTokenList()
        val postfixNotation = buildPostfixNotation(tokenList)
        println(postfixNotation.joinToString(" "))
        val expression = convertToExpression(postfixNotation)
        println(expression)
        val result = evaluate(expression)
        println(result)
        println()
        result
    }.sum()
    println(sum)
}

fun buildPostfixNotation(tokenList: List<Token>): ArrayDeque<Token> {
    val operations = ArrayDeque<OperationToken>()
    val postfixNotation = ArrayDeque<Token>()
    tokenList.forEach { token ->
        if (token is NumberToken) {
            postfixNotation += token
        } else {
            val newOperator = token as OperationToken
            when (token) {
                AdditionToken, MultiplicationToken -> {
                    fun moveOn(): Boolean {
                        val topOperator = operations.lastOrNull() ?: return false
                        return topOperator.isOperation &&
                                topOperator.precedence >= newOperator.precedence
                    }
                    while (moveOn()) {
                        postfixNotation += operations.removeLast()
                    }
                    operations += newOperator
                }
                OpeningParenthesis -> {
                    operations += token
                }
                ClosingParenthesis -> {
                    while (operations.isNotEmpty() && operations.last() != OpeningParenthesis) {
                        postfixNotation += operations.removeLast()
                    }
                    if (operations.lastOrNull() == OpeningParenthesis) {
                        operations.removeLast()
                    }
                }
            }
        }
    }
    postfixNotation += operations.reversed()
    return postfixNotation
}

sealed class Token(private val s: String) {
    override fun toString() = s
}

enum class Operation {
    PLUS, MULT
}

fun OperationToken.toOperation(): Operation = when (this) {
    AdditionToken -> PLUS
    MultiplicationToken -> MULT
    else -> throw IllegalStateException("Can't convert $this to operation")
}

sealed class OperationToken(
    s: String,
    val isOperation: Boolean,
    val precedence: Int
) : Token(s)

object OpeningParenthesis : OperationToken("(", false, 3)
object ClosingParenthesis : OperationToken(")", false, 3)
object AdditionToken : OperationToken("+", true, 2)
object MultiplicationToken : OperationToken("*", true, 1)
class NumberToken(val value: Int) : Token(value.toString())

fun String.toTokenList(): List<Token> =
    replace("(", "( ").replace(")", " )").split(" ").map {
        if (it.length > 1) throw IllegalStateException("Expecting whitespaces between tokens: $it")
        val ch = it.first()
        if (ch.isDigit())
            NumberToken(ch - '0')
        else when (ch) {
            '(' -> OpeningParenthesis
            ')' -> ClosingParenthesis
            '*' -> MultiplicationToken
            '+' -> AdditionToken
            else -> throw IllegalStateException("Unexpected token: $ch")
        }
    }

sealed class Expression
data class NumberExpression(val value: Int) : Expression()
data class BinaryExpression(
    val operation: Operation,
    val left: Expression,
    val right: Expression,
) : Expression()

fun convertToExpression(postfixNotation: List<Token>): Expression {
    val expressions = ArrayDeque<Expression>()
    for (token in postfixNotation) {
        if (token is NumberToken) {
            expressions += NumberExpression(token.value)
        } else {
            token as OperationToken
            val operation = token.toOperation()
            val right = expressions.removeLast()
            val left = expressions.removeLast()
            expressions += BinaryExpression(operation, left, right)
        }
    }
    if (expressions.size != 1) throw IllegalStateException("More operations on stack: $expressions")
    return expressions.single()
}

fun evaluate(expression: Expression): Long = when (expression) {
    is NumberExpression -> expression.value.toLong()
    is BinaryExpression -> when (expression.operation) {
        PLUS -> evaluate(expression.left) + evaluate(expression.right)
        MULT -> evaluate(expression.left) * evaluate(expression.right)
    }
}