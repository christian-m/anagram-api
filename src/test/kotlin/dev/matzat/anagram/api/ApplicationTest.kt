package dev.matzat.anagram.api

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import dev.matzat.anagram.api.model.ComparisonResponse
import dev.matzat.anagram.module
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
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class ApplicationTest {
    @Test
    fun testReadyPath() =
        testApplication {
            application {
                module()
            }
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
        testApplication {
            application {
                module()
            }
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
        testApplication {
            application {
                module()
            }
            val response = client.get("/openapi.json")
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
            }
        }

    @Test
    fun testSwaggerUiPath() =
        testApplication {
            application {
                module()
            }
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
    ) = testApplication {
        application {
            module()
        }
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
                // prop(HttpResponse::contentType).isEqualTo(ContentType.Application.Json.toString())
            }
            assertThat(responseBody).all {
                prop(ComparisonResponse::text).isEqualTo(givenText)
                prop(ComparisonResponse::candidate).isEqualTo(givenCandidate)
                prop(ComparisonResponse::result).isEqualTo(expectedResult)
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
