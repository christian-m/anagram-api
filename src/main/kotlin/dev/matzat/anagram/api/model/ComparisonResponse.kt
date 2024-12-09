package dev.matzat.anagram.api.model

import dev.matzat.anagram.service.ComparisonResult
import kotlinx.serialization.Serializable

@Serializable
data class ComparisonResponse(
    val text: String,
    val candidate: String,
    val result: ComparisonResult,
)
