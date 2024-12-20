package dev.matzat.anagram.service

import dev.matzat.anagram.persistence.dao.AnagramDao
import dev.matzat.anagram.service.ComparisonResult.ANAGRAM
import dev.matzat.anagram.service.ComparisonResult.EQUAL
import dev.matzat.anagram.service.ComparisonResult.NO_MATCH
import io.ktor.util.toCharArray
import java.security.MessageDigest

class AnagramService(
    private val anagramDao: AnagramDao,
) {
    fun compare(
        text: String,
        other: String,
    ): ComparisonResult =
        when {
            (text.isEmpty() || other.isEmpty()) -> NO_MATCH
            (text.lowercase() == other.lowercase()) -> EQUAL
            (text.normalize() == other.normalize()) -> ANAGRAM
            else -> NO_MATCH
        }.also {
            if (text.isNotEmpty()) {
                anagramDao.addAnagram(text.normalize().md5(), text)
            }
            if (other.isNotEmpty()) {
                anagramDao.addAnagram(other.normalize().md5(), other)
            }
        }

    fun findInHistory(text: String): List<String> =
        anagramDao
            .findAnagramHistory(text.normalize().md5())
            ?.filter { it.anagram != text }
            ?.map { it.anagram }
            .orEmpty()

    private fun String.normalize(): String =
        lowercase()
            .toCharArray()
            .filter { it.isLetter() }
            .sorted()
            .joinToString()

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(this.toByteArray())
        return digest.toHex()
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }.uppercase()
}

enum class ComparisonResult {
    ANAGRAM,
    EQUAL,
    NO_MATCH,
}
