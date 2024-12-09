package dev.matzat.anagram.service

import dev.matzat.anagram.service.ComparisonResult.ANAGRAM
import dev.matzat.anagram.service.ComparisonResult.EQUAL
import dev.matzat.anagram.service.ComparisonResult.NO_MATCH
import io.ktor.util.toCharArray

class AnagramService {
    fun compare(
        text: String,
        other: String,
    ): ComparisonResult =
        when {
            (text.isEmpty() || other.isEmpty()) -> NO_MATCH
            (text.lowercase() == other.lowercase()) -> EQUAL
            (text.normalize() == other.normalize()) -> ANAGRAM
            else -> NO_MATCH
        }

    private fun String.normalize(): String =
        lowercase()
            .toCharArray()
            .filter { it.isLetter() }
            .sorted()
            .joinToString()
}

enum class ComparisonResult {
    ANAGRAM,
    EQUAL,
    NO_MATCH,
}
