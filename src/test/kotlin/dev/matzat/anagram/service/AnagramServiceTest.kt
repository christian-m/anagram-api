package dev.matzat.anagram.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

internal class AnagramServiceTest {
    private val service = AnagramService()

    // Sample data taken from Wikipedia, see https://en.wikipedia.org/wiki/Anagram
    @ParameterizedTest
    @CsvSource(
        value = [
            "evil,vile",
            "a gentleman,elegant man",
            "silent,listen",
            "restful,fluster",
            "cheater,teacher",
            "funeral,real fun",
            "adultery,true lady",
            "forty five,over fifty",
            "Santa,Satan",
            "William Shakespeare,I am a weakish speller",
            "Madam Curie,Radium came",
            "George Bush,He bugs Gore",
            "Tom Marvolo Riddle,I am Lord Voldemort",
            "The Morse code,Here come dots",
            "New York Times,monkeys write",
            "Church of Scientology,rich-chosen goofy cult",
            "McDonald's restaurants,Uncle Sam's standard rot",
            "coronavirus,carnivorous",
            "She Sells Sanctuary,Santa; shy less cruel",
            "She Sells Sanctuary,Satan; cruel less shy",
        ],
    )
    @DisplayName("GIVEN a text and an anagram WHEN those are compared THEN result ANAGRAM is returned")
    fun testAnagram(
        text: String,
        other: String,
    ) {
        assertThat(service.compare(text, other)).isEqualTo(ComparisonResult.ANAGRAM)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "evil", "vile",
            "a gentleman", "elegant man",
            "silent", "listen",
            "restful", "fluster",
            "cheater", "teacher",
            "funeral", "real fun",
            "adultery", "true lady",
            "forty five", "over fifty",
            "Santa", "Satan",
            "William Shakespeare", "I am a weakish speller",
            "Madam Curie", "Radium came",
            "George Bush", "He bugs Gore",
            "Tom Marvolo Riddle", "I am Lord Voldemort",
            "The Morse code", "Here come dots",
            "New York Times", "monkeys write",
            "Church of Scientology", "rich-chosen goofy cult",
            "McDonald's restaurants", "Uncle Sam's standard rot",
            "coronavirus", "carnivorous",
            "She Sells Sanctuary", "Santa; shy less cruel",
            "She Sells Sanctuary", "Satan; cruel less shy",
        ],
    )
    @DisplayName("GIVEN two equal texts WHEN those are compared THEN result EQUAL is returned")
    fun testEqual(text: String) {
        // modify the other string slightly, so there is a difference in casing but not in letters
        val other = text.substring(0..0).uppercase() + text.substring(1..text.length - 1).lowercase()
        assertThat(service.compare(text, other)).isEqualTo(ComparisonResult.EQUAL)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "evil,village",
            "a gentleman,elegance",
            "silent,listener",
            "restful,flutter",
            "cheater,torch",
            "funeral,reality",
            "adultery,sure lady",
            "forty five,over sixty",
            "Santa,Claus",
            "William Shakespeare,I am a english speaker",
            "Madam Curie,Radium core",
            "George Bush,He bugs George",
            "Tom Marvolo Riddle,I am Lord Waterworld",
            "The Morse code,Here come darts",
            "New York Times,monkeys writing",
            "Church of Scientology,rich-chosen cargo cult",
            "McDonald's restaurants,Uncle Sam's standard burgers",
            "coronavirus,marburg virus",
        ],
    )
    @DisplayName(
        "GIVEN two texts that are no anagrams of each other WHEN those are compared THEN result NO_MATCH is returned",
    )
    fun testNoAnagram(
        text: String,
        other: String,
    ) {
        assertThat(service.compare(text.trim(), other.trim())).isEqualTo(ComparisonResult.NO_MATCH)
    }

    @ParameterizedTest
    @MethodSource("nullableValuesTestData")
    @DisplayName("GIVEN empty values WHEN those are compared THEN result NO_MATCH is returned")
    fun testNullAreNoAnagram(
        text: String,
        other: String,
    ) {
        assertThat(service.compare(text, other)).isEqualTo(ComparisonResult.NO_MATCH)
    }

    companion object {
        @JvmStatic
        private fun nullableValuesTestData(): Stream<Arguments> {
            fun arguments(
                text: String?,
                other: String?,
            ) = Arguments.of(
                text,
                other,
            )

            return Stream.of(
                arguments(text = "", other = "other"),
                arguments(text = "text", other = ""),
                arguments(text = "", other = ""),
            )
        }
    }
}
