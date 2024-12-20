package dev.matzat.anagram.service

import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import dev.matzat.anagram.DbTestBase
import dev.matzat.anagram.persistence.dao.AnagramDao
import dev.matzat.anagram.persistence.domain.AnagramHashes
import dev.matzat.anagram.persistence.domain.Anagrams
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

internal class AnagramServiceTest : DbTestBase() {
    private val service = AnagramService(AnagramDao())

    @AfterEach
    internal fun tearDown() {
        transaction {
            Anagrams.deleteAll()
            AnagramHashes.deleteAll()
        }
    }

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

    @ParameterizedTest(name = "{index}: {0}")
    @DisplayName("GIVEN some inputs and a text WHEN the text is searched in the historyTHEN matching anagrams are returned")
    @MethodSource("anagramHistoryTestData")
    fun testAnagramHistory(
        @Suppress("unused") desc: String,
        givenInputs: List<Pair<String, String>>,
        givenText: String,
        expectedResult: List<String>,
    ) {
        givenInputs.forEach { service.compare(it.first, it.second) }

        assertThat(service.findInHistory(givenText)).all {
            hasSize(expectedResult.size)
            containsExactlyInAnyOrder(*expectedResult.toTypedArray())
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() = setupDatabaseConnection()

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

        @JvmStatic
        private fun anagramHistoryTestData(): Stream<Arguments> {
            fun arguments(
                desc: String,
                givenInputs: List<Pair<String, String>>,
                givenText: String,
                expectedResult: List<String>,
            ) = Arguments.of(
                desc,
                givenInputs,
                givenText,
                expectedResult,
            )

            return Stream.of(
                arguments(
                    desc = "search for listen in the history",
                    givenInputs = listOf(Pair("listen", "enlist"), Pair("listen", "restful"), Pair("listen", "silent")),
                    givenText = "listen",
                    expectedResult = listOf("enlist", "silent"),
                ),
                arguments(
                    desc = "search for enlist in the history",
                    givenInputs = listOf(Pair("listen", "enlist"), Pair("listen", "restful"), Pair("listen", "silent")),
                    givenText = "enlist",
                    expectedResult = listOf("listen", "silent"),
                ),
                arguments(
                    desc = "search for silent in the history",
                    givenInputs = listOf(Pair("listen", "enlist"), Pair("listen", "restful"), Pair("listen", "silent")),
                    givenText = "silent",
                    expectedResult = listOf("listen", "enlist"),
                ),
                arguments(
                    desc = "search for restful in the history",
                    givenInputs = listOf(Pair("listen", "enlist"), Pair("listen", "restful"), Pair("listen", "silent")),
                    givenText = "restful",
                    expectedResult = emptyList(),
                ),
            )
        }
    }
}
