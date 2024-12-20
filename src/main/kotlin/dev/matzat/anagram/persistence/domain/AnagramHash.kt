package dev.matzat.anagram.persistence.domain

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object AnagramHashes : UUIDTable("anagram_hashes") {
    val anagramHash = text("anagram_hash").uniqueIndex()
}

class AnagramHash(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AnagramHash>(AnagramHashes)

    @Suppress("MemberNameEqualsClassName")
    var anagramHash by AnagramHashes.anagramHash
    val anagrams by Anagram referrersOn Anagrams.anagramHash
}
