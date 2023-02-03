package com.sildian.apps.togetrail.common.core

import kotlin.random.Random

private const val RANDOM_STRING_MAX_SIZE = 20

private val lowerCaseAlphaChars: CharRange = ('a'..'z')
private val upperCaseAlphaChars: CharRange = ('A'..'Z')
private val numericalChars: CharRange = ('0'..'9')

fun Random.nextString(
    size: Int = nextInt(until = RANDOM_STRING_MAX_SIZE)
): String {
    val availableChars = lowerCaseAlphaChars + upperCaseAlphaChars + numericalChars
    return nextString(size = size, availableChars = availableChars)
}

fun Random.nextAlphaString(
    size: Int = nextInt(until = RANDOM_STRING_MAX_SIZE)
): String {
    val availableChars = lowerCaseAlphaChars + upperCaseAlphaChars
    return nextString(size = size, availableChars = availableChars)
}

fun Random.nextNumericalString(
    size: Int = nextInt(until = RANDOM_STRING_MAX_SIZE)
): String {
    val availableChars = numericalChars.toList()
    return nextString(size = size, availableChars = availableChars)
}

private fun Random.nextString(
    size: Int,
    availableChars: List<Char>,
): String {
    return List(size = size) {
        availableChars.random()
    }.joinToString(separator = "") { it.toString() }
}