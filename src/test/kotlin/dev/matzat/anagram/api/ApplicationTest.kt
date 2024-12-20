package dev.matzat.anagram.api

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import dev.matzat.anagram.DbTestBase
import dev.matzat.anagram.api.model.ComparisonResponse
import dev.matzat.anagram.api.model.HistoryResponse
import dev.matzat.anagram.service.ComparisonResult
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class ApplicationTest : DbTestBase() {
    @Test
    fun testReadyPath() =
        withTestApplication {
            val response = client.get("/ready")
            val responseBody = response.bodyAsText()
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
                assertThat(responseBody).isEqualTo("{}")
            }
        }

    @Test
    fun testHealthPath() =
        withTestApplication {
            val response = client.get("/health")
            val responseBody = response.bodyAsText()
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
                assertThat(responseBody).isEqualTo("{}")
            }
        }

    @Test
    fun testSwaggerOpenApiPath() =
        withTestApplication {
            val response = client.get("/openapi.json")
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
            }
        }

    @Test
    fun testSwaggerUiPath() =
        withTestApplication {
            val response = client.get("/swagger-ui")
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
            }
        }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("anagramTestData")
    fun testComparePath(
        @Suppress("unused") desc: String,
        givenText: String,
        givenCandidate: String,
        expectedResult: ComparisonResult,
    ) = withTestApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        val response =
            client.post("/compare") {
                header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("text" to givenText, "candidate" to givenCandidate).formUrlEncode())
            }
        val responseBody = response.body<ComparisonResponse>()
        assertAll {
            assertThat(response).all {
                prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
            }
            assertThat(responseBody).all {
                prop(ComparisonResponse::text).isEqualTo(givenText)
                prop(ComparisonResponse::candidate).isEqualTo(givenCandidate)
                prop(ComparisonResponse::result).isEqualTo(expectedResult)
            }
        }
    }

    @Test
    fun testHistoryPath() =
        withTestApplication {
            val client = createClient { install(ContentNegotiation) { json() } }
            val givenText = "enlist"
            val expectedResult = listOf("silent", "listen")
            client.post("/compare") {
                header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("text" to givenText, "candidate" to expectedResult.first()).formUrlEncode())
            }
            client.post("/compare") {
                header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("text" to givenText, "candidate" to expectedResult.last()).formUrlEncode())
            }
            val response =
                client.post("/history") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(listOf("text" to givenText).formUrlEncode())
                }
            val responseBody = response.body<HistoryResponse>()
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
                assertThat(responseBody).all {
                    prop(HistoryResponse::text).isEqualTo(givenText)
                    prop(HistoryResponse::anagrams).all {
                        hasSize(2)
                        containsExactlyInAnyOrder(*expectedResult.toTypedArray())
                    }
                }
            }
        }

    @Test
    fun testEmptyHistoryPath() =
        withTestApplication {
            val client = createClient { install(ContentNegotiation) { json() } }
            val givenText = "trouble"
            val response =
                client.post("/history") {
                    header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(listOf("text" to givenText).formUrlEncode())
                }
            val responseBody = response.body<HistoryResponse>()
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
                assertThat(responseBody).all {
                    prop(HistoryResponse::text).isEqualTo(givenText)
                    prop(HistoryResponse::anagrams).isEmpty()
                }
            }
        }

    companion object {
        @JvmStatic
        private fun anagramTestData(): Stream<Arguments> {
            fun arguments(
                @Suppress("unused") desc: String,
                givenText: String,
                givenCandidate: String,
                expectedResult: ComparisonResult,
            ) = Arguments.of(
                desc,
                givenCandidate,
                givenText,
                expectedResult,
            )

            return Stream.of(
                arguments(
                    desc = "equal input",
                    givenText = "text",
                    givenCandidate = "text",
                    expectedResult = ComparisonResult.EQUAL,
                ),
                arguments(
                    desc = "inputs are an anagram",
                    givenText = "gramana",
                    givenCandidate = "anagram",
                    expectedResult = ComparisonResult.ANAGRAM,
                ),
                arguments(
                    desc = "different input",
                    givenText = "text",
                    givenCandidate = "anagram",
                    expectedResult = ComparisonResult.NO_MATCH,
                ),
            )
        }
    }
}
