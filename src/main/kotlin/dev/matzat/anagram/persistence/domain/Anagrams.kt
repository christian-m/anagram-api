package dev.matzat.anagram.persistence.domain

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object Anagrams : UUIDTable("anagrams") {
    val anagramHash = reference("anagram_hash_id", AnagramHashes, ReferenceOption.CASCADE)
    val anagram = text("anagram")
}

class Anagram(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Anagram>(Anagrams)

    var anagramHash by AnagramHash referencedOn Anagrams.anagramHash

    @Suppress("MemberNameEqualsClassName")
    var anagram by Anagrams.anagram
}
