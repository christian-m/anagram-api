package dev.matzat.anagram.api

import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import dev.matzat.anagram.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test

internal class ApplicationTest {
    @Test
    fun testRootPath() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/")
            val responseBody = response.bodyAsText()
            assertAll {
                assertThat(response).all {
                    prop(HttpResponse::status).isEqualTo(HttpStatusCode.OK)
                }
                assertThat(responseBody).isEqualTo("Welcome to the Anagram API")
            }
        }

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
}
