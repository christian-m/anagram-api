package dev.matzat.anagram.api.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    val text: String,
    val anagrams: List<String>,
)
