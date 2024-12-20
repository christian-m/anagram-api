package dev.matzat.anagram.persistence.dao

import dev.matzat.anagram.persistence.domain.Anagram
import dev.matzat.anagram.persistence.domain.AnagramHash
import dev.matzat.anagram.persistence.domain.AnagramHashes
import org.jetbrains.exposed.sql.transactions.transaction

class AnagramDao {
    fun addAnagram(
        anagramHash: String,
        anagram: String,
    ) = transaction {
        val anagramHash =
            AnagramHash.find { AnagramHashes.anagramHash eq anagramHash }.firstOrNull()
                ?: AnagramHash.Companion.new {
                    this.anagramHash = anagramHash
                }
        if (anagramHash.anagrams.none { it.anagram == anagram }) {
            Anagram.new {
                this.anagramHash = anagramHash
                this.anagram = anagram
            }
        }
    }

    fun findAnagramHistory(anagramHash: String) =
        transaction {
            AnagramHash
                .find { AnagramHashes.anagramHash eq anagramHash }
                .firstOrNull()
                ?.anagrams
                ?.toList()
        }
}
